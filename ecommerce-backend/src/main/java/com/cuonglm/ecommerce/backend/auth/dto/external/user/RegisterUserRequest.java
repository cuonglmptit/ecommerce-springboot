package com.cuonglm.ecommerce.backend.auth.dto.external.user;

import jakarta.validation.constraints.*;

/**
 * RegisterUserRequest – Lớp nhận json để đăng ký người dùng mới của endpoint /register.
 *
 * <p>
 * Nhận dữ liệu từ json trong request body sang DTO này để sử dụng trong controller.
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 08 August 2025
 */
public record RegisterUserRequest(
        @NotBlank(message = "Tên đăng nhập không được để trống")
        @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự")
        @Pattern(regexp = "^[a-zA-Z0-9_.]+$", message = "Username chỉ được chứa chữ cái, số, dấu gạch dưới và dấu chấm")
        String username,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Địa chỉ email không hợp lệ")
        @Size(max = 100, message = "Email không vượt quá 100 ký tự")
        String email
) {
}
