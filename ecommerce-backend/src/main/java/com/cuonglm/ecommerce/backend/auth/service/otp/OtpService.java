package com.cuonglm.ecommerce.backend.auth.service.otp;

import com.cuonglm.ecommerce.backend.auth.dto.otp.VerifyOtpRequestDTO;
import com.cuonglm.ecommerce.backend.auth.entity.OtpToken;
import com.cuonglm.ecommerce.backend.auth.enums.OtpChannel;
import com.cuonglm.ecommerce.backend.auth.enums.OtpPurpose;

/**
 * OtpService – Interface xử lý OTP.
 *
 * <p>
 * Interface này cung cấp các phương thức để tạo, gửi và xác minh OTP.
 * </p>
 *
 * @author cuonglmptit
 * @since Tuesday, 04 November 2025
 */

public interface OtpService {
    /**
     *
     * Tạo mã OTP mới, vô hiệu hóa mã cũ và lưu vào DB.
     *
     * @param target  Mã định danh người nhận.
     * @param purpose Mục đích sử dụng OTP.
     * @param channel Kênh gửi Otp
     * @return
     */
    OtpToken generateOtp(String target, OtpPurpose purpose, OtpChannel channel);

    /**
     * Gửi mã OTP (raw) đến người dùng thông qua kênh đã xác định.
     * @param otpToken OtpToken đã lưu trong DB (chứa hash).
     * @param rawOtpCode Mã OTP thô (chưa hash) để gửi đi.
     * @return true nếu gửi thành công.
     */
    boolean sendOtp(OtpToken otpToken, String rawOtpCode);

    /**
     * Xác thực mã OTP do người dùng cung cấp.
     *
     * @param otpToken OtpToken Entity cần xác thực.
     * @return boolean True nếu mã hợp lệ, ngược lại False.
     */
    boolean validateOtp(VerifyOtpRequestDTO otpToken);

    /**
     * Phương thức tiện ích để thực hiện cả 3 bước (Generate, Save Channel, Send).
     *
     * @param target  Mã định danh người nhận.
     * @param purpose Mục đích sử dụng OTP.
     * @param channel Kênh gửi OTP.
     * @return OtpToken đã được tạo.
     */
    OtpToken generateAndSendOtp(String target, OtpPurpose purpose, OtpChannel channel);

    /**
     * <b>Vô hiệu hóa tất cả các mã OTP cũ còn hiệu lực</b>
     * * <p>
     * * <b>Mục đích (Bảo mật):</b> Khi người dùng yêu cầu một mã OTP mới (ví dụ: nhấn "Gửi lại OTP"), phương thức này
     * * sẽ được gọi <b>trước khi</b> tạo mã mới. Nó đảm bảo rằng tất cả các mã OTP cũ đã gửi trước đó (nhưng vẫn còn hạn)
     * * sẽ bị vô hiệu hóa ngay lập tức. Điều này ngăn chặn người dùng vô tình hoặc kẻ tấn công cố ý sử dụng một mã OTP cũ.
     *
     * @param target  Mã định danh người nhận (email/SĐT).
     * @param purpose Mục đích sử dụng OTP (ví dụ: REGISTER).
     * @return Số lượng bản ghi (mã OTP) đã được vô hiệu hóa.
     */
    int invalidateAllActiveTokens(String target, OtpPurpose purpose);
}
