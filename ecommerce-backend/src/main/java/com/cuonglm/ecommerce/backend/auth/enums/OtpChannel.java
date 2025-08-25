package com.cuonglm.ecommerce.backend.auth.enums;

/**
 * OtpChannel – Định nghĩa kênh gửi mã OTP.
 *
 * <p>
 * Enum này giúp xác định phương tiện mà mã OTP được gửi đến người dùng
 * (Ví dụ: Email, SMS), hỗ trợ logic trong EmailService, SMSService,...
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 05 November 2025
 */
public enum OtpChannel {

    /**
     * Mã OTP được gửi qua thư điện tử (Email).
     */
    EMAIL,

    /**
     * Mã OTP được gửi qua tin nhắn văn bản SMS.
     */
    SMS,

    /**
     * Mã OTP được tạo và hiển thị trong ứng dụng xác thực bên thứ ba (Ví dụ: Google Authenticator - TOTP).
     */
    AUTHENTICATOR_APP
}