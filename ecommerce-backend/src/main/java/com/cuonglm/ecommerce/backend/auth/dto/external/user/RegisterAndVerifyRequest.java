package com.cuonglm.ecommerce.backend.auth.dto.external.user;

import com.cuonglm.ecommerce.backend.auth.enums.OtpPurpose;
import jakarta.validation.constraints.*;

/**
 * RegisterAndVerifyRequest – DTO cho yêu cầu xác thực OTP VÀ đăng ký tài khoản.
 *
 * <p>
 * DTO này được sử dụng ở Bước 2 của luồng Verify-Then-Create. Nó chứa toàn bộ thông tin đăng ký
 * cùng với mã OTP để xác thực.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 12 November 2025
 */
public record RegisterAndVerifyRequest(
        @NotBlank(message = "Tên đăng nhập không được để trống")
        @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự")
        @Pattern(regexp = "^[a-zA-Z0-9_.]+$", message = "Username chỉ được chứa chữ cái, số, dấu gạch dưới và dấu chấm")
        String username,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Địa chỉ email không hợp lệ")
        @Size(max = 100, message = "Email không vượt quá 100 ký tự")
        String email,

        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 8, max = 50, message = "Mật khẩu phải từ 8 đến 50 ký tự")
        String password,

        @NotBlank(message = "Mã OTP không được để trống")
        @Size(min = 6, max = 6, message = "Mã OTP phải có đúng 6 chữ số")
        String otp,

        @NotNull(message = "Mục đích OTP không được để trống")
        OtpPurpose purpose
) {
}