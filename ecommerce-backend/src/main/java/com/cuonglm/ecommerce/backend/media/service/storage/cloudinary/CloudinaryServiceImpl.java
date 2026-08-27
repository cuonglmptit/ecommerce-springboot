package com.cuonglm.ecommerce.backend.media.service.storage.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cuonglm.ecommerce.backend.core.utils.FileUtils;
import com.cuonglm.ecommerce.backend.media.dto.external.CloudResourceDTO;
import com.cuonglm.ecommerce.backend.media.enums.MediaProvider;
import com.cuonglm.ecommerce.backend.media.service.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * CloudinaryServiceImpl – Triển khai logic các phương thức của {@link StorageService} sử dụng Cloudinary.
 *
 * @author cuonglmptit
 * @since Tuesday, 25 November 2025
 */
@Service
public class CloudinaryServiceImpl implements StorageService {
    private final Cloudinary cloudinary;
    private final CloudinaryConfig cloudinaryConfig;


    public CloudinaryServiceImpl(Cloudinary cloudinary, CloudinaryConfig cloudinaryConfig) {
        this.cloudinary = cloudinary;
        this.cloudinaryConfig = cloudinaryConfig;
    }

    @Override
    public MediaProvider getProvider() {
        return MediaProvider.CLOUDINARY;
    }

    @Override
    public CloudResourceDTO upload(MultipartFile file, String folder) {
        try {
            String fileName = file.getOriginalFilename();
            String publicId = (fileName != null && !fileName.isEmpty())
                    ? FileUtils.generateUniquePublicId(fileName)
                    : UUID.randomUUID().toString();

            Map<?, ?> options = ObjectUtils.asMap(
                    "folder", folder,
                    "public_id", publicId,
                    // Tự động tối ưu hóa chất lượng ảnh và chuyển đổi định dạng
                    "quality", "auto:best",
                    "fetch_format", "auto",
                    // Chỉ cho phép upload file Image và Video
                    "resource_type", "auto"
            );

            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

            return new CloudResourceDTO(
                    (String) uploadResult.get("secure_url"),
                    (String) uploadResult.get("public_id"),
                    (String) uploadResult.get("format")
            );
        } catch (IOException e) {
            throw new RuntimeException("Lỗi I/O khi upload file lên Cloudinary: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String externalId) {
        try {
            cloudinary.uploader().destroy(externalId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            System.err.println("Lỗi khi xóa media trên Cloudinary: " + externalId + ". Lỗi: " + e.getMessage());
        }
    }

    @Override
    public String generateSignature(Map<String, Object> params) {
        return cloudinary.apiSignRequest(
                params,
                this.cloudinary.config.apiSecret,
                this.cloudinary.config.signatureVersion
        );
    }

    @Override
    public boolean verifyCloudinaryResponse(String publicId, Long version, String signature) {
        if (publicId == null || version == null || signature == null) {
            return false;
        }

        // Cloudinary response signature formula: SHA1("public_id=...&version=..." + api_secret)
        Map<String, Object> paramsToVerify = new HashMap<>();
        paramsToVerify.put("public_id", publicId);
        paramsToVerify.put("version", version);

        String expectedSignature = cloudinary.apiSignRequest(
                paramsToVerify,
                cloudinaryConfig.getApiSecret(),
                this.cloudinary.config.signatureVersion
        );

        return expectedSignature.equalsIgnoreCase(signature);
    }

    @Override
    public String getApiKey() {
        return cloudinaryConfig.getApiKey();
    }

    @Override
    public String getDirectUploadUrl() {
        return String.format("https://api.cloudinary.com/v1_1/%s/auto/upload", cloudinaryConfig.getCloudName());
    }
}
