package com.cuonglm.ecommerce.backend.media.service;

import com.cuonglm.ecommerce.backend.core.exception.ValidationErrorsException;
import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
import com.cuonglm.ecommerce.backend.media.dto.external.CloudResourceDTO;
import com.cuonglm.ecommerce.backend.media.dto.external.MediaResponse;
import com.cuonglm.ecommerce.backend.media.dto.internal.MediaInfoDTO;
import com.cuonglm.ecommerce.backend.media.entity.Media;
import com.cuonglm.ecommerce.backend.media.enums.MediaFormat;
import com.cuonglm.ecommerce.backend.media.enums.MediaType;
import com.cuonglm.ecommerce.backend.media.repository.MediaRepository;
import com.cuonglm.ecommerce.backend.media.service.storage.StorageService;
import com.cuonglm.ecommerce.backend.user.dto.internal.UserInfoDTO;
import com.cuonglm.ecommerce.backend.user.entity.User;
import com.cuonglm.ecommerce.backend.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * MediaServiceImpl – Triển khai logic các phương thức của {@link MediaService}.
 *
 * @author cuonglmptit
 * @since Monday, 24 November 2025
 */
@Service
@Transactional
public class MediaServiceImpl implements MediaService {
    // Giới hạn dung lượng theo loại tài nguyên
    private static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024;  // 10MB cho Ảnh
    private static final long MAX_VIDEO_SIZE = 50L * 1024 * 1024;  // 50MB cho Video

    // Danh sách MIME Types được phép tải lên
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/svg+xml"
    );
    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
            "video/mp4", "video/webm", "video/quicktime", "video/x-msvideo"
    );

    private final StorageService storageService;
    private final MediaRepository mediaRepository;
    private final UserService userService;

    public MediaServiceImpl(StorageService storageService,
                            MediaRepository mediaRepository,
                            UserService userService) {
        this.storageService = storageService;
        this.mediaRepository = mediaRepository;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MediaInfoDTO> findMediaInfoById(UUID id) {
        if (id == null) return Optional.empty();
        return mediaRepository.findMediaInfoById(id)
                .map(MediaInfoDTO::fromView);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaInfoDTO> findMediaInfoByIds(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return mediaRepository.findMediaInfoByIdIn(ids)
                .stream()
                .map(MediaInfoDTO::fromView)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Media getMediaReference(UUID mediaId) {
        return mediaRepository.getReferenceById(mediaId);
    }

    @Override
    public MediaResponse uploadProductMedia(MultipartFile file) {
        // 1. Validate gom tất cả các lỗi vào ValidationErrorsException
        validateFile(file);

        // 2. Lấy User đang đăng nhập
        UserInfoDTO currentUser = userService.getCurrentAuthenticatedUserInfo();

        // 3. Upload lên dịch vụ Cloudinary
        String folder = String.format("ecommerce/products/user-%d/", currentUser.id());
        CloudResourceDTO resource = storageService.upload(file, folder);

        // 4. Lưu Metadata vào Asset Registry (bảng media)
        Media media = new Media();

        User userRef = new User();
        userRef.setId(currentUser.id());
        media.setUploader(userRef);

        media.setUrl(resource.url());
        media.setExternalId(resource.externalId());
        String originalFilename = file.getOriginalFilename();
        media.setTitle(originalFilename != null ? originalFilename : "media-asset");

        // Tự động phân định loại file (IMAGE hay VIDEO)
        MediaFormat mediaFormat = mapStringToMediaFormat(resource.format());
        media.setFormat(mediaFormat);
        media.setType(mediaFormat.getMediaType());
        media.setProvider(storageService.getProvider());
        media.setStatus(BasicStatus.ACTIVE);

        Media savedMedia = mediaRepository.save(media);

        // 5. Trả về Response
        return MediaResponse.fromEntity(savedMedia);
    }

    // --- Helper ---
    private void validateFile(MultipartFile file) {
        Map<String, Object> errors = new HashMap<>();

        if (file == null || file.isEmpty()) {
            errors.put("file", "File tải lên không được để trống.");
            throw new ValidationErrorsException("Lỗi tải file", errors);
        }

        // Tra cứu trực tiếp từ MediaFormat SSOT
        Optional<MediaFormat> formatOpt = MediaFormat.fromMimeType(file.getContentType());

        if (formatOpt.isEmpty()) {
            errors.put("contentType", "Định dạng file không được hỗ trợ. Chỉ chấp nhận: "
                    + MediaFormat.getAllowedExtensionsDisplay(MediaType.IMAGE, MediaType.VIDEO));
        } else {
            MediaFormat format = formatOpt.get();
            if (format.isImage() && file.getSize() > MAX_IMAGE_SIZE) {
                errors.put("size", String.format("Dung lượng ảnh (%d MB) vượt quá giới hạn tối đa (10MB).", file.getSize() / (1024 * 1024)));
            } else if (format.isVideo() && file.getSize() > MAX_VIDEO_SIZE) {
                errors.put("size", String.format("Dung lượng video (%d MB) vượt quá giới hạn tối đa (50MB).", file.getSize() / (1024 * 1024)));
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationErrorsException("Dữ liệu file tải lên không hợp lệ.", errors);
        }
    }

    private MediaFormat mapStringToMediaFormat(String formatString) {
        // Tự động tìm theo extension từ Cloudinary trả về, nếu không có fallback về JPG
        return MediaFormat.fromExtension(formatString).orElse(MediaFormat.JPG);
    }
}
