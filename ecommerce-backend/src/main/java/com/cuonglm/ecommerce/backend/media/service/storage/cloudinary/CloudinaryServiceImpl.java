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

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public MediaProvider getProvider() {
        return MediaProvider.CLOUDINARY;
    }

    @Override
    public CloudResourceDTO upload(MultipartFile file, String folder) {
        try {
            // Tạo tên file chuẩn hóa (Public ID)
            // Nếu file.getOriginalFilename() null thì dùng UUID thuần
            String fileName = file.getOriginalFilename();
            String publicId = (fileName != null && !fileName.isEmpty())
                    ? FileUtils.generateUniquePublicId(fileName)
                    : UUID.randomUUID().toString();

            Map options = ObjectUtils.asMap(
                    "folder", folder,
                    "public_id", publicId,
                    // Tự động tối ưu hóa chất lượng ảnh và chuyển đổi định dạng
                    "quality", "auto:best",
                    "fetch_format", "auto",
                    // Chỉ cho phép upload file Image và Video
                    "resource_type", "auto"
            );

            // Thực hiện upload và nhận kết quả
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

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
            // ObjectUtils.emptyMap() vì việc xóa không cần options phức tạp
            Map destroyResult = cloudinary.uploader().destroy(externalId, ObjectUtils.emptyMap());
            System.out.println("Kết quả thực hiện xóa media trên Cloudinary: " + externalId + ": "
                    + (String) destroyResult.get("result"));
        } catch (IOException e) {
            System.err.println("Lỗi khi xóa media trên Cloudinary: " + externalId + ". Lỗi: " + e.getMessage());
        }
    }

}
