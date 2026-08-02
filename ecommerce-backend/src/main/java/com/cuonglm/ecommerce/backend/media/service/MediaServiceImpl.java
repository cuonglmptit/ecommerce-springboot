package com.cuonglm.ecommerce.backend.media.service;

import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
import com.cuonglm.ecommerce.backend.media.dto.external.CloudResourceDTO;
import com.cuonglm.ecommerce.backend.media.dto.external.MediaCreateResponseDTO;
import com.cuonglm.ecommerce.backend.media.dto.internal.MediaInfoDTO;
import com.cuonglm.ecommerce.backend.media.entity.Media;
import com.cuonglm.ecommerce.backend.media.enums.MediaFormat;
import com.cuonglm.ecommerce.backend.media.repository.MediaRepository;
import com.cuonglm.ecommerce.backend.media.service.storage.StorageService;
import com.cuonglm.ecommerce.backend.user.dto.internal.UserInfoDTO;
import com.cuonglm.ecommerce.backend.user.entity.User;
import com.cuonglm.ecommerce.backend.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

/**
 * MediaServiceImpl – Triển khai logic các phương thức của {@link MediaService}.
 *
 * @author cuonglmptit
 * @since Monday, 24 November 2025
 */
@Service
@Transactional
public class MediaServiceImpl implements MediaService {
    private final StorageService storageService;
    private final MediaRepository mediaRepository;
    private final UserService userService;

    public MediaServiceImpl(StorageService storageService, MediaRepository mediaRepository, UserService userService) {
        this.storageService = storageService;
        this.mediaRepository = mediaRepository;
        this.userService = userService;
    }

    @Override
    public Optional<MediaInfoDTO> findMediaInfoById(UUID id) {
        return mediaRepository.findMediaInfoById(id)
                .map(view -> new MediaInfoDTO(
                                view.getId(),
                                view.getType(),
                                view.getStatus(),
                                view.getUploader_Id()
                        )
                );
    }

    @Override
    public Media getMediaReference(UUID mediaId) {
        return mediaRepository.getReferenceById(mediaId);
    }

    @Override
    public MediaCreateResponseDTO uploadProductMedia(MultipartFile file) {
        // 1. Kiểm tra nghiệp vụ cơ bản (Validation)
        if (file.isEmpty()) {
            // throw new BadRequestException("File không được rỗng.");
            throw new RuntimeException("File không được rỗng.");
        }

        // 2. Lấy User đang thực hiện upload
        UserInfoDTO currentUser = userService.getCurrentAuthenticatedUserInfo();

        // 3. Upload lên dịch vụ thứ 3 (Cloudinary)
        CloudResourceDTO resource;
        try {
            // Đặt thư mục theo cấu trúc: ecommerce/products/user-{id}/
            String folder = String.format("ecommerce/products/user-%d/", currentUser.id());
            resource = storageService.upload(file, folder);
        } catch (Exception e) {
            // Xử lý lỗi upload lên Cloud
            throw new RuntimeException("Lỗi khi upload file lên dịch vụ lưu trữ: " + e.getMessage());
        }

        // 4. Lưu Entity Media vào DB
        Media media = new Media();

        User userRef = new User();
        userRef.setId(currentUser.id());
        media.setUploader(userRef);
        media.setUrl(resource.url());
        media.setExternalId(resource.externalId());
        String originalFilename = file.getOriginalFilename();
        media.setTitle(originalFilename != null ? originalFilename : "unknown-file");

        // Logic ánh xạ Enum
        MediaFormat mediaFormat = mapStringToMediaFormat(resource.format());
        media.setFormat(mediaFormat);
        media.setType(mediaFormat.getMediaType());
        media.setProvider(storageService.getProvider());

        media.setStatus(BasicStatus.ACTIVE);

        Media savedMedia = mediaRepository.save(media);

        // 5. Trả về Response
        return new MediaCreateResponseDTO(
                savedMedia.getId(),
                savedMedia.getUrl(),
                savedMedia.getAlt(),
                savedMedia.getTitle(),
                savedMedia.getExternalId(),
                savedMedia.getFormat()
        );
    }

    private MediaFormat mapStringToMediaFormat(String formatString) {
        if (formatString == null) return MediaFormat.JPG;
        try {
            return MediaFormat.valueOf(formatString.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Giả định nếu format không hợp lệ, trả về JPG hoặc xử lý lỗi
            return MediaFormat.JPG;
        }
    }
}
