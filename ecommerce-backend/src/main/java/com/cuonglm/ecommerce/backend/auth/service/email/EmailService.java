package com.cuonglm.ecommerce.backend.auth.service.email;

import java.util.concurrent.CompletableFuture;

/**
 * EmailService – Interface cho dịch vụ gửi mail.
 *
 * <p>
 * Interface này định nghĩa các phương thức cơ bản cho việc gửi email cho người dùng như gửi OTP, thông báo,...
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 05 November 2025
 */
public interface EmailService {
    /**
     * Gửi một email bất đồng bộ.
     *
     * @param to Địa chỉ người nhận.
     * @param subject Chủ đề email.
     * @param text Nội dung email (thường là HTML).
     * @return CompletableFuture<Void> báo hiệu quá trình gửi email.
     */
    CompletableFuture<Void> sendEmail(String to, String subject, String text);

    /**
     * Tạo template email cho OTP.
     *
     * @param name Tên người dùng.
     * @param otp Mã OTP.
     * @param purpose Mục đích sử dụng.
     * @return Chuỗi HTML nội dung email.
     */
    String buildOtpEmailContent(String name, String otp, String purpose);
}
