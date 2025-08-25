package com.cuonglm.ecommerce.backend.auth.enums;

/**
 * OtpPurpose – Định nghĩa mục đích sử dụng của mã OTP.
 *
 * <p>
 * Enum này giúp liên kết một mã OTP với một mục đích nghiệp vụ cụ thể
 * (Ví dụ: Đăng nhập, Chuyển tiền), đảm bảo mã không bị sử dụng sai mục đích.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 05 November 2025
 */
public enum OtpPurpose {

    /**
     * Dùng để xác thực người dùng trong quá trình Đăng nhập (Log In).
     */
    LOGIN,

    /**
     * Dùng để xác minh quyền sở hữu khi Đăng ký tài khoản mới.
     */
    REGISTER,

    /**
     * Dùng để xác minh quyền sở hữu khi Đặt lại Mật khẩu (Reset Password).
     */
    RESET_PASSWORD,
}