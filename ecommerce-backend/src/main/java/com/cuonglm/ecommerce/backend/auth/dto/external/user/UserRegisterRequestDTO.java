package com.cuonglm.ecommerce.backend.auth.dto.external.user;

import com.cuonglm.ecommerce.backend.user.enums.UserRole;
import jakarta.validation.constraints.*;

/**
 * UserRegisterRequestDTO – Lớp nhận json để đăng ký người dùng mới của endpoint /register.
 *
 * <p>
 * Nhận dữ liệu từ json trong request body sang DTO này để sử dụng trong controller.
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 08 August 2025
 */
public record UserRegisterRequestDTO(
        @NotBlank(message = "Tên người dùng không được để trống")
        @Size(min = 3, max = 50, message = "Tên người dùng phải từ 3 đến 50 ký tự")
        String username,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Địa chỉ email không hợp lệ")
        @Size(max = 100, message = "Email không vượt quá 100 ký tự")
        String email
) {
}
