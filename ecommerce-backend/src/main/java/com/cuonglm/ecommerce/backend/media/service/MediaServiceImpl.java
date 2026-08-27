package com.cuonglm.ecommerce.backend.media.service;

import com.cuonglm.ecommerce.backend.core.exception.ValidationErrorsException;
import com.cuonglm.ecommerce.backend.core.utils.SecurityUtils;
import com.cuonglm.ecommerce.backend.media.dto.external.*;
import com.cuonglm.ecommerce.backend.media.dto.internal.MediaInfoDTO;
import com.cuonglm.ecommerce.backend.media.entity.Media;
import com.cuonglm.ecommerce.backend.media.enums.MediaFormat;
import com.cuonglm.ecommerce.backend.media.enums.MediaScope;
import com.cuonglm.ecommerce.backend.media.enums.MediaType;
import com.cuonglm.ecommerce.backend.media.projection.MediaInfoView;
import com.cuonglm.ecommerce.backend.media.repository.MediaRepository;
import com.cuonglm.ecommerce.backend.media.service.storage.StorageService;
import com.cuonglm.ecommerce.backend.media.service.storage.cloudinary.CloudinaryConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private static final String PUBLIC_FOLDER = "ecommerce/public";
    private static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024;  // 10MB cho Ảnh
    private static final long MAX_VIDEO_SIZE = 50L * 1024 * 1024;  // 50MB cho Video
    private static final long TOKEN_VALIDITY_SECONDS = 900L;

    private final StorageService storageService;
    private final MediaRepository mediaRepository;
    private final CloudinaryConfig cloudinaryConfig;

    public MediaServiceImpl(StorageService storageService,
                            MediaRepository mediaRepository,
                            CloudinaryConfig cloudinaryConfig) {
        this.storageService = storageService;
        this.mediaRepository = mediaRepository;
        this.cloudinaryConfig = cloudinaryConfig;
    }

    @Override
    public PrepareUploadResponse prepareDirectUpload(PrepareUploadRequest request) {
        validateFileMetadata(request.contentType(), request.fileSize());
        MediaScope scope = request.getResolvedScope();

        // 1. Kiểm tra trùng lặp Checksum bằng Projection (Rule #1)
        if (scope == MediaScope.PUBLIC) {
            Optional<MediaInfoView> existing = mediaRepository.findMediaInfoByChecksum(request.checksum());
            if (existing.isPresent()) {
                MediaInfoView media = existing.get();
                return PrepareUploadResponse.existing(media.getId(), media.getUrl());
            }
        }

        // 2. Định tuyến thư mục theo Scope
        Long currentUserId = SecurityUtils.getRequiredCurrentUserId();
        String folder = (scope == MediaScope.PUBLIC) ? PUBLIC_FOLDER : "ecommerce/private/user-" + currentUserId;
        String publicId = (scope == MediaScope.PUBLIC) ? request.checksum() : UUID.randomUUID().toString();

        long timestamp = System.currentTimeMillis() / 1000L;
        Map<String, Object> paramsToSign = new HashMap<>();
        paramsToSign.put("folder", folder);
        paramsToSign.put("public_id", publicId);
        paramsToSign.put("timestamp", timestamp);

        // Ký chữ ký gửi sang Cloudinary
        String signature = storageService.generateSignature(paramsToSign);

        // Sinh Vé ủy quyền nội bộ (HMAC-SHA256) bảo vệ Backend
        String uploadToken = generateInternalUploadToken(request.checksum(), currentUserId, timestamp, scope);

        return PrepareUploadResponse.directUpload(
                storageService.getDirectUploadUrl(),
                storageService.getApiKey(),
                timestamp,
                signature,
                folder,
                publicId,
                uploadToken
        );
    }

    @Override
    public MediaResponse confirmDirectUpload(ConfirmUploadRequest request) {
        Long currentUserId = SecurityUtils.getRequiredCurrentUserId();
        MediaScope scope = request.getResolvedScope();
        Map<String, Object> errors = new LinkedHashMap<>();

        // 1. BẮT BUỘC XÁC THỰC VỀ UỶ QUYỀN (HMAC-SHA256): Chống hacker gọi trực tiếp không qua prepare
        boolean isTokenValid = verifyInternalUploadToken(
                request.uploadToken(),
                request.checksum(),
                currentUserId,
                request.timestamp(),
                scope
        );
        if (!isTokenValid) {
            errors.put("uploadToken", "Phiên tải lên không hợp lệ, bị can thiệp hoặc đã quá hạn.");
        }

        // 2. BẮT BUỘC XÁC THỰC CHỮ KÝ PHẢN HỒI TỪ CLOUDINARY: Chứng minh Cloudinary đã thực sự nhận file
        boolean isSignatureValid = storageService.verifyCloudinaryResponse(
                request.externalId(),
                request.version(),
                request.signature()
        );
        if (!isSignatureValid) {
            errors.put("signature", "Chữ ký phản hồi từ CDN không hợp lệ. Yêu cầu bị từ chối do nghi vấn can thiệp dữ liệu.");
        }

        // 3. BẮT BUỘC KIỂM TRA ĐỊNH DẠNG PHÂN VÙNG EXTERNAL ID
        String expectedPrefix = (scope == MediaScope.PUBLIC) ? PUBLIC_FOLDER : "ecommerce/private/user-" + currentUserId;
        if (!request.externalId().startsWith(expectedPrefix)) {
            errors.put("externalId", "Định danh tài nguyên không đúng phân vùng lưu trữ hợp lệ.");
        }

        // 4. BẮT BUỘC RÀNG BUỘC CHECKSUM VÀ EXTERNAL ID TRONG PUBLIC SCOPE
        if (scope == MediaScope.PUBLIC && !request.externalId().equals(PUBLIC_FOLDER + "/" + request.checksum())) {
            errors.put("externalId", "External ID không khớp với mã Checksum SHA-256 của file.");
        }

        // 5. KIỂM TRA FORMAT MAGIC BYTES
        Optional<MediaFormat> formatOpt = MediaFormat.fromExtension(request.format());
        if (formatOpt.isEmpty()) {
            errors.put("format", "Định dạng file từ máy chủ CDN không được hỗ trợ: " + request.format());
        }

        // 6. KIỂM TRA DUNG LƯỢNG THỰC TẾ
        if (request.size() > MAX_IMAGE_SIZE && formatOpt.isPresent() && formatOpt.get().isImage()) {
            errors.put("size", "Dung lượng ảnh thực tế vượt quá giới hạn 10MB.");
        }

        // NẾU CÓ BẤT KỲ LỖI NÀO -> CHIỀU THEO ZERO-TRUST, TỪ CHỐI TOÀN BỘ
        if (!errors.isEmpty()) {
            throw new ValidationErrorsException("Xác thực tải lên thất bại do vi phạm an toàn dữ liệu.", errors);
        }

        // 7. Chống race-condition
        if (scope == MediaScope.PUBLIC) {
            Optional<MediaInfoView> existing = mediaRepository.findMediaInfoByChecksum(request.checksum());
            if (existing.isPresent()) {
                return MediaResponse.fromView(existing.get());
            }
        }

        // 8. Lưu Database qua Factory Method
        Media media = Media.of(
                request.checksum(),
                scope,
                request.url(),
                request.externalId(),
                request.originalFilename(),
                request.alt(),
                formatOpt.get(),
                storageService.getProvider()
        );

        Media saved = mediaRepository.save(media);
        return MediaResponse.fromEntity(saved);
    }

    @Override
    public MediaResponse uploadProductMedia(MultipartFile file) {
        validateFile(file);
        String checksum = calculateRealSha256(file);

        Optional<Media> existing = mediaRepository.findByChecksum(checksum);
        if (existing.isPresent()) {
            return MediaResponse.fromEntity(existing.get());
        }

        CloudResourceDTO resource = storageService.upload(file, PUBLIC_FOLDER);
        MediaFormat mediaFormat = MediaFormat.fromExtension(resource.format()).orElse(MediaFormat.JPG);

        Media media = Media.of(
                checksum,
                MediaScope.PUBLIC,
                resource.url(),
                resource.externalId(),
                file.getOriginalFilename(),
                null,
                mediaFormat,
                storageService.getProvider()
        );

        Media savedMedia = mediaRepository.save(media);
        return MediaResponse.fromEntity(savedMedia);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MediaInfoDTO> findMediaInfoById(UUID id) {
        if (id == null) return Optional.empty();
        return mediaRepository.findMediaInfoById(id).map(MediaInfoDTO::fromView);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaInfoDTO> findMediaInfoByIds(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return mediaRepository.findMediaInfoByIdIn(ids).stream().map(MediaInfoDTO::fromView).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Media getMediaReference(UUID mediaId) {
        return mediaRepository.getReferenceById(mediaId);
    }

    // --- Helpers ---

    private String generateInternalUploadToken(String checksum, Long userId, Long timestamp, MediaScope scope) {
        String rawData = String.format("%s:%d:%d:%s", checksum, userId, timestamp, scope.name());
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    cloudinaryConfig.getApiSecret().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            hmac.init(secretKey);
            byte[] hash = hmac.doFinal(rawData.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khởi tạo mã bảo mật nội bộ", e);
        }
    }

    private boolean verifyInternalUploadToken(String token, String checksum, Long userId, Long timestamp, MediaScope scope) {
        if (token == null || timestamp == null) return false;

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        if (currentTimestamp - timestamp > TOKEN_VALIDITY_SECONDS || timestamp > currentTimestamp + 60) {
            return false;
        }

        String expectedToken = generateInternalUploadToken(checksum, userId, timestamp, scope);
        return MessageDigest.isEqual(
                expectedToken.getBytes(StandardCharsets.UTF_8),
                token.getBytes(StandardCharsets.UTF_8)
        );
    }

    private void validateFileMetadata(String contentType, Long fileSize) {
        Map<String, Object> errors = new HashMap<>();
        if (fileSize == null || fileSize <= 0) {
            errors.put("fileSize", "Kích thước file không hợp lệ.");
        }
        Optional<MediaFormat> formatOpt = MediaFormat.fromMimeType(contentType);
        if (formatOpt.isEmpty()) {
            errors.put("contentType", "Định dạng file không được hỗ trợ. Chỉ chấp nhận: "
                    + MediaFormat.getAllowedExtensionsDisplay(MediaType.IMAGE, MediaType.VIDEO));
        } else {
            MediaFormat format = formatOpt.get();
            if (format.isImage() && fileSize > MAX_IMAGE_SIZE) {
                errors.put("fileSize", String.format("Dung lượng ảnh (%d MB) vượt quá giới hạn 10MB.", fileSize / (1024 * 1024)));
            } else if (format.isVideo() && fileSize > MAX_VIDEO_SIZE) {
                errors.put("fileSize", String.format("Dung lượng video (%d MB) vượt quá giới hạn 50MB.", fileSize / (1024 * 1024)));
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationErrorsException("Dữ liệu file tải lên không hợp lệ.", errors);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationErrorsException("Lỗi tải file", Map.of("file", "File tải lên không được để trống."));
        }
        validateFileMetadata(file.getContentType(), file.getSize());
    }

    private String calculateRealSha256(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException("Không thể tính checksum SHA-256 cho file.", e);
        }
    }

    private MediaFormat mapStringToMediaFormat(String formatString) {
        return MediaFormat.fromExtension(formatString).orElse(MediaFormat.JPG);
    }
}
