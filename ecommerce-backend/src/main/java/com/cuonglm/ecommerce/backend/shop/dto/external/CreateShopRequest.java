package com.cuonglm.ecommerce.backend.shop.dto.external;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ShopCreateRequestDTO – Dto cho việc tạo shop.

 * @author cuonglmptit
 * @since Wednesday, 19 November 2025
 */
public record CreateShopRequest(
        @NotBlank(message = "Tên Shop không được để trống")
        @Size(min = 3, max = 255, message = "Tên Shop phải dài từ 3 đến 255 ký tự")
        String name,

        @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
        String description,

        /**
         * ID của người sở hữu Shop (Chỉ Admin mới có quyền truyền trường này để tạo hộ).
         * Nếu là User bình thường, Backend tự động gán ID của người đang đăng nhập.
         */
        Long ownerId
) {
}
