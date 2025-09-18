package com.cuonglm.ecommerce.backend.auth.service;

import com.cuonglm.ecommerce.backend.auth.dto.external.otp.ResendOtpRequestDTO;
import com.cuonglm.ecommerce.backend.auth.dto.external.otp.VerifyOtpRequestDTO;
import com.cuonglm.ecommerce.backend.auth.dto.external.user.RegisterAndVerifyRequestDTO;
import com.cuonglm.ecommerce.backend.auth.dto.external.user.UserRegisterRequestDTO;
import com.cuonglm.ecommerce.backend.auth.entity.OtpToken;
import com.cuonglm.ecommerce.backend.auth.enums.OtpChannel;
import com.cuonglm.ecommerce.backend.auth.enums.OtpPurpose;
import com.cuonglm.ecommerce.backend.auth.service.otp.OtpService;
import com.cuonglm.ecommerce.backend.core.exception.ValidationErrorsException;
import com.cuonglm.ecommerce.backend.user.dto.internal.UserConflictInfoDTO;
import com.cuonglm.ecommerce.backend.user.dto.internal.UserCreationDTO;
import com.cuonglm.ecommerce.backend.user.dto.internal.UserCreationResultDTO;
import com.cuonglm.ecommerce.backend.user.enums.UserRole;
import com.cuonglm.ecommerce.backend.user.enums.UserStatus;
import com.cuonglm.ecommerce.backend.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AuthServiceImpl – Triển khai logic cho {@link AuthService}.
 *
 * <p>
 * Triển khai logic cho các nghiệp vụ xác thực chính như đăng ký, đăng nhập, quên mật khẩu,...
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 08 August 2025
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserService userService, OtpService otpService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Yêu cầu gửi OTP để tạo tài khoản và kiểm tra trùng lặp.
     *
     * @param request DTO chứa username, email, password của người dùng.
     * @return OtpToken đã được tạo.
     */
    @Override
    public OtpToken requestRegistrationOtp(UserRegisterRequestDTO request) {
        String username = request.username();
        String email = request.email();

        // 1. Kiểm tra User đã tồn tại chưa
        List<UserConflictInfoDTO> conflicts = userService.findConflictUsers(username, email);
        Map<String, Object> errors = new HashMap<>();

        for (UserConflictInfoDTO u : conflicts) {
            if (u.username().equalsIgnoreCase(username)) {
                errors.put("username", "Username đã tồn tại");
            }
            if (u.email().equals(email)) {
                errors.put("email", "Email đã tồn tại");
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationErrorsException("Thông tin đăng ký không hợp lệ", errors);
        }

        return otpService.generateAndSendOtp(email, OtpPurpose.REGISTER, OtpChannel.EMAIL);
    }

    /**
     * Xác thực OTP và tạo User Entity.
     *
     * @param request DTO {@link RegisterAndVerifyRequestDTO} chứa các thông tin cần thiết để xác thực.
     */
    @Override
    @Transactional // Đảm bảo việc tạo User và vô hiệu hóa OTP là atomic
    public UserCreationResultDTO verifyOtpAndRegisterLocalUser(RegisterAndVerifyRequestDTO request) {
        // 1. Xác thực mã OTP
        VerifyOtpRequestDTO verifyRequest = new VerifyOtpRequestDTO(
                request.email(),
                request.otp(),
                OtpPurpose.REGISTER
        );

        // Nếu xác thực thất bại, AuthException sẽ được ném ra từ otpService
        if (!otpService.validateOtp(verifyRequest)) {
            throw new IllegalArgumentException("Mã OTP không hợp lệ hoặc đã hết hạn.");
        }

        // 3. Hash mật khẩu và chuyển đổi sang DTO cho User Domain
        String hashedPassword = passwordEncoder.encode(request.password());
        UserCreationDTO userCreationDTO = new UserCreationDTO(
                request.username(),
                request.email(),
                true,
                hashedPassword,
                UserRole.CUSTOMER,
                UserStatus.ACTIVE
        );

        // 4. Gọi UserService để tạo Entity (UserService trả về UserCreationResultDTO)
        UserCreationResultDTO result = userService.createLocalUser(userCreationDTO);
        System.out.println("DEBUG: User created successfully: " + result.userId() + ", " + result.username() + ", " + result.email());

        // 5. Vô hiệu hóa tất cả các token OTP đang hoạt động của email này (chống replay attack)
        otpService.invalidateAllActiveTokens(request.email(), OtpPurpose.REGISTER);

        return result;
    }

    @Override
    public void resendOtp(ResendOtpRequestDTO request) {
        if (request.purpose() == OtpPurpose.REGISTER) {
            if (userService.existsByEmail(request.target())) {
                throw new IllegalStateException("Tài khoản với email " + request.target() + " đã được đăng ký và kích hoạt thành công. Vui lòng đăng nhập.");
            }
            // Nếu không tìm thấy user hoặc user chưa ACTIVE, tiến hành gửi lại OTP mới.
            otpService.generateAndSendOtp(request.target(), request.purpose(), OtpChannel.EMAIL);

        } else {
            // Gửi lại OTP cho các mục đích khác (ví dụ: RESET_PASSWORD, LOGIN)
            // Đối với các mục đích này, thường yêu cầu User phải tồn tại và ACTIVE.
            // Có thể thêm logic kiểm tra User tồn tại/ACTIVE ở đây nếu cần.
            otpService.generateAndSendOtp(request.target(), request.purpose(), OtpChannel.EMAIL);
        }
    }
}
