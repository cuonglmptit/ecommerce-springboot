package com.cuonglm.ecommerce.backend.auth.dto.otp;

import com.cuonglm.ecommerce.backend.auth.enums.OtpPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


/**
 * DTO cho request xác thực OTP.
 * Bắt buộc phải có 'otp'.
 *
 * @param target  Định danh của tài khoản cần xác thực (email hoặc số điện thoại).
 * @param otp     Mã OTP người dùng nhập.
 * @param purpose Mục đích của OTP (ví dụ: REGISTER_VERIFICATION).
 */
public record VerifyOtpRequestDTO(
        // Các trường này đã được khai báo validation trong OtpRequest.
        // Ta có thể lặp lại @Valid ở Controller nếu muốn, nhưng về mặt ngữ nghĩa là ổn.
        @NotBlank(message = "Định danh (email/số điện thoại) không được để trống")
        String target,

        @NotBlank(message = "Mã OTP không được để trống")
        @Size(min = 6, max = 6, message = "Mã OTP phải có 6 chữ số")
        String otp, // Trường đặc trưng cho việc Verify

        @NotNull(message = "Mục đích không được để trống")
        OtpPurpose purpose
) implements OtpRequest {} // **TRIỂN KHAI INTERFACE**