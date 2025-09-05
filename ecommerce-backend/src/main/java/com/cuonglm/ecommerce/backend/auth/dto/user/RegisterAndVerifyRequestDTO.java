package com.cuonglm.ecommerce.backend.auth.dto.user;

import com.cuonglm.ecommerce.backend.auth.enums.OtpPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * RegisterAndVerifyRequestDTO – DTO cho yêu cầu xác thực OTP VÀ đăng ký tài khoản.
 *
 * <p>
 * DTO này được sử dụng ở Bước 2 của luồng Verify-Then-Create. Nó chứa toàn bộ thông tin đăng ký
 * cùng với mã OTP để xác thực.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 12 November 2025
 */
public record RegisterAndVerifyRequestDTO(
        @NotBlank(message = "Tên người dùng không được để trống")
        @Size(min = 3, max = 50, message = "Tên người dùng phải từ 3 đến 50 ký tự")
        String username,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Địa chỉ email không hợp lệ")
        @Size(max = 100, message = "Email không vượt quá 100 ký tự")
        String email,

        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 8, max = 50, message = "Mật khẩu phải từ 8 đến 50 ký tự")
        String password, // Mật khẩu thô (raw password)

        @NotBlank(message = "Mã OTP không được để trống")
        @Size(min = 6, max = 6, message = "Mã OTP phải có 6 chữ số")
        String otp,

        @NotNull(message = "Mục đích không được để trống")
        OtpPurpose purpose // Phải là REGISTER
) {}