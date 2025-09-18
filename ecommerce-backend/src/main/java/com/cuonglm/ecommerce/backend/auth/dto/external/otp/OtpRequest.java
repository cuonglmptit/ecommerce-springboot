package com.cuonglm.ecommerce.backend.auth.dto.external.otp;

import com.cuonglm.ecommerce.backend.auth.enums.OtpPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * OtpRequest – Lớp Interface chung cho một request về OTP
 *
 * <p>
 * Một request OTP luôn cần có target và purpose.
 * </p>
 *
 * @author cuonglmptit
 * @since Thursday, 06 November 2025
 */
public interface OtpRequest {

    // Đảm bảo mọi request OTP đều có các trường này (và các annotation validation)
    @NotBlank(message = "Định danh (target) không được để trống")
    String target();

    @NotNull(message = "Mục đích (purpose) không được để trống")
    OtpPurpose purpose();
}
