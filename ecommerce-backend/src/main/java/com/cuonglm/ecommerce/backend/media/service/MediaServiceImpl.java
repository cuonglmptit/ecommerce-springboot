package com.cuonglm.ecommerce.backend.media.service;

import com.cuonglm.ecommerce.backend.core.exception.ValidationErrorsException;
import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
import com.cuonglm.ecommerce.backend.core.utils.SecurityUtils;
import com.cuonglm.ecommerce.backend.media.dto.external.CloudResourceDTO;
import com.cuonglm.ecommerce.backend.media.dto.external.ConfirmUploadRequest;
import com.cuonglm.ecommerce.backend.media.dto.external.MediaResponse;
import com.cuonglm.ecommerce.backend.media.dto.external.PrepareUploadRequest;
import com.cuonglm.ecommerce.backend.media.dto.external.PrepareUploadResponse;
import com.cuonglm.ecommerce.backend.media.dto.internal.MediaInfoDTO;
import com.cuonglm.ecommerce.backend.media.entity.Media;
import com.cuonglm.ecommerce.backend.media.enums.MediaFormat;
import com.cuonglm.ecommerce.backend.media.enums.MediaScope;
import com.cuonglm.ecommerce.backend.media.enums.MediaType;
import com.cuonglm.ecommerce.backend.media.repository.MediaRepository;
import com.cuonglm.ecommerce.backend.media.service.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    private final StorageService storageService;
    private final MediaRepository mediaRepository;

    public MediaServiceImpl(StorageService storageService, MediaRepository mediaRepository) {
        this.storageService = storageService;
        this.mediaRepository = mediaRepository;
    }

    @Override
    public PrepareUploadResponse prepareDirectUpload(PrepareUploadRequest request) {
        validateFileMetadata(request.contentType(), request.fileSize());
        MediaScope scope = request.getResolvedScope();

        // 1. Nếu là PUBLIC: Kiểm tra trùng Checksum (Instant Upload 0s)
        if (scope == MediaScope.PUBLIC) {
            Optional<Media> existing = mediaRepository.findByChecksum(request.checksum());
            if (existing.isPresent()) {
                Media media = existing.get();
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

        String signature = storageService.generateSignature(paramsToSign);

        return PrepareUploadResponse.directUpload(
                storageService.getDirectUploadUrl(),
                storageService.getApiKey(),
                timestamp,
                signature,
                folder,
                publicId
        );
    }

    @Override
    public MediaResponse confirmDirectUpload(ConfirmUploadRequest request) {
        MediaScope scope = request.getResolvedScope();

        // Chống race condition cho file PUBLIC
        if (scope == MediaScope.PUBLIC) {
            Optional<Media> existing = mediaRepository.findByChecksum(request.checksum());
            if (existing.isPresent()) {
                return MediaResponse.fromEntity(existing.get());
            }
        }

        Media media = new Media();
        media.setChecksum(request.checksum());
        media.setScope(scope);
        media.setUrl(request.url());
        media.setExternalId(request.externalId());
        media.setTitle(request.originalFilename() != null ? request.originalFilename() : "media-asset");
        media.setAlt(request.alt());

        MediaFormat mediaFormat = mapStringToMediaFormat(request.format());
        media.setFormat(mediaFormat);
        media.setType(mediaFormat.getMediaType());
        media.setProvider(storageService.getProvider());
        media.setStatus(BasicStatus.ACTIVE);

        // AuditMetadata tự động nhận createdById qua Spring Auditing
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

        Media media = new Media();
        media.setChecksum(checksum);
        media.setScope(MediaScope.PUBLIC);
        media.setUrl(resource.url());
        media.setExternalId(resource.externalId());
        media.setTitle(file.getOriginalFilename() != null ? file.getOriginalFilename() : "media-asset");

        MediaFormat mediaFormat = mapStringToMediaFormat(resource.format());
        media.setFormat(mediaFormat);
        media.setType(mediaFormat.getMediaType());
        media.setProvider(storageService.getProvider());
        media.setStatus(BasicStatus.ACTIVE);

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
