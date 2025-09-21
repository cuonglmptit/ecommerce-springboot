package com.cuonglm.ecommerce.backend.shop.dto.external;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ShopCreateRequestDTO – Dto cho việc tạo shop.

 * @author cuonglmptit
 * @since Wednesday, 19 November 2025
 */
public class ShopCreateRequestDTO {
    @NotBlank(message = "Tên Shop không được để trống")
    @Size(min = 3, max = 255, message = "Tên Shop phải dài từ 3 đến 255 ký tự")
    private String name;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String description;

    /**
     * ID của người sở hữu Shop (Chỉ được phép dùng bởi ADMIN)
     * Nếu không điền, người sở hữu mặc định là người dùng đang đăng nhập.
     */
    private Long ownerId;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getOwnerId() {
        return ownerId;
    }
}
