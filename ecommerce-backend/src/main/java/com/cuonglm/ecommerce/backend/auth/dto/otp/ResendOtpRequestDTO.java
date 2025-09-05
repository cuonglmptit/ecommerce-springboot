package com.cuonglm.ecommerce.backend.auth.dto.otp;

import com.cuonglm.ecommerce.backend.auth.enums.OtpPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * ResendOtpRequestDTO – DTO cho việc yêu cầu tạo OTP mới.
 *
 * <p>
 * DTO cho request Gửi Lại OTP Không cần trường 'otp'.
 * </p>
 *
 * @author cuonglmptit
 * @since Thursday, 06 November 2025
 */
public record ResendOtpRequestDTO(
        @NotBlank(message = "Định danh (target) không được để trống")
        String target,

        @NotNull(message = "Mục đích (purpose) không được để trống")
        OtpPurpose purpose
) implements OtpRequest {
}