package com.cuonglm.ecommerce.backend.media.service.storage;

import com.cuonglm.ecommerce.backend.media.dto.external.CloudResourceDTO;
import com.cuonglm.ecommerce.backend.media.enums.MediaProvider;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * StorageService – Interface mô tả các phương thức thao tác với dịch vụ lưu trữ thứ 3 như Cloudinary.
 *
 * @author cuonglmptit
 * @since Tuesday, 25 November 2025
 */
public interface StorageService {
    /**
     * Upload file lên dịch vụ lưu trữ.
     * @param file File ảnh/video
     * @param folder Đường dẫn thư mục trên Cloud (ví dụ: "ecommerce/products/")
     * @return Thông tin về tài nguyên đã upload
     */
    CloudResourceDTO upload(MultipartFile file, String folder);

    /**
     * Xóa tài nguyên theo External ID.
     * @param externalId ID tài nguyên từ dịch vụ lưu trữ
     */
    void delete(String externalId);

    /**
     * Lấy ra provider
     * @return
     */
    MediaProvider getProvider();

    /**
     * Tạo chữ ký xác thực bảo mật cho Client upload trực tiếp (Direct Signed Upload).
     */
    String generateSignature(Map<String, Object> paramsToSign);

    /**
     * Lấy API Key công khai của dịch vụ lưu trữ.
     */
    String getApiKey();

    /**
     * Lấy URL tiếp nhận upload trực tiếp từ Client.
     */
    String getDirectUploadUrl();

    boolean verifyCloudinaryResponse(String publicId, Long version, String signature);
}
