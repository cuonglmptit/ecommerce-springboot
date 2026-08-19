package com.cuonglm.ecommerce.backend.shop.dto.external;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * UpdateShopRequest – DTO nhận dữ liệu cập nhật thông tin Cửa hàng.
 *
 * @author cuonglmptit
 * @since Wednesday, 19 August 2026
 */
public record UpdateShopRequest(
        @NotBlank(message = "Tên Shop không được để trống")
        @Size(min = 3, max = 255, message = "Tên Shop phải dài từ 3 đến 255 ký tự")
        String name,

        @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
        String description
) {
}