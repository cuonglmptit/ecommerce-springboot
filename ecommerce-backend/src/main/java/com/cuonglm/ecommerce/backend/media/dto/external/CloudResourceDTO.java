package com.cuonglm.ecommerce.backend.media.dto.external;

/**
 * CloudResource – Dto chứa thông tin chi tiết về tài nguyên sau khi được upload lên Cloud. *
 *
 * @author cuonglmptit
 * @since Tuesday, 25 November 2025
 */
public record CloudResourceDTO(
        /**
         * Đường dẫn URL công khai của tài nguyên
         */
        String url,
        /**
         * Public ID hoặc External ID từ dịch vụ lưu trữ (cần cho việc xoá)
         */
        String externalId,
        /**
         * Định dạng file (jpg, png, mp4, v.v.)
         */
        String format
) {
}
