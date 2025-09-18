package com.cuonglm.ecommerce.backend.auth.service;

import com.cuonglm.ecommerce.backend.auth.dto.external.otp.ResendOtpRequestDTO;
import com.cuonglm.ecommerce.backend.auth.dto.external.user.RegisterAndVerifyRequestDTO;
import com.cuonglm.ecommerce.backend.auth.dto.external.user.UserRegisterRequestDTO;
import com.cuonglm.ecommerce.backend.auth.entity.OtpToken;
import com.cuonglm.ecommerce.backend.user.dto.internal.UserCreationResultDTO;

/**
 * AuthService – Interface xử lý các nghiệp vụ xác thực chính.
 *
 * <p>
 * Interface này định nghĩa các phương thức cơ bản cho việc xác thực người dùng như đăng nhập, đăng ký, quên mật khẩu,...
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 08 August 2025
 */
public interface AuthService {
    /**
     * Yêu cầu gửi mã OTP để xác minh email/số điện thoại trước khi tạo tài khoản.
     *
     * <p>
     * 1. Kiểm tra username/email có bị trùng lặp không (sử dụng UserConflictInfoDTO).
     * 2. Nếu không trùng, tạo và gửi OTP đến email/số điện thoại.
     * </p>
     *
     * @param request DTO chứa username, email, password của người dùng.
     */
    OtpToken requestRegistrationOtp(UserRegisterRequestDTO request);

    /**
     * Yêu cầu tạo OTP mới
     *
     * @param request DTO {@link ResendOtpRequestDTO} chứa mục đích và target.
     */
    void resendOtp(ResendOtpRequestDTO request);

    /**
     * Kiểm tra OTP và tạo người dùng local mới
     * <p>
     * Nếu như OTP hợp lệ thì sẽ tạo tạo khoàn người dùng từ thông tin đăng ký.
     * </p>
     *
     * @param request DTO {@link RegisterAndVerifyRequestDTO} chứa các thông tin cần thiết để xác thực.
     * @return Thông tin tài khoản được tạo thành công.
     */
    UserCreationResultDTO verifyOtpAndRegisterLocalUser(RegisterAndVerifyRequestDTO request);
}
