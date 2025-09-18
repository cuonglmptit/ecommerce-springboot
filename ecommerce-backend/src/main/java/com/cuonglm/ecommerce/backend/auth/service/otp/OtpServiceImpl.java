package com.cuonglm.ecommerce.backend.auth.service.otp;

import com.cuonglm.ecommerce.backend.auth.dto.external.otp.VerifyOtpRequestDTO;
import com.cuonglm.ecommerce.backend.auth.entity.OtpToken;
import com.cuonglm.ecommerce.backend.auth.enums.OtpChannel;
import com.cuonglm.ecommerce.backend.auth.enums.OtpPurpose;
import com.cuonglm.ecommerce.backend.auth.repository.OtpTokenRepository;
import com.cuonglm.ecommerce.backend.auth.service.email.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * OtpServiceImpl - Triển khai logic cho {@link OtpService}.
 *
 * <p>
 * Triển khai logic tạo và xác thực OTP như tạo Otp, gửi Otp, xác thực Otp.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 05 November 2025
 */
@Service
public class OtpServiceImpl implements OtpService {
    private final OtpTokenRepository otpTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // Cấu hình cho OTP
    private static final long OTP_VALIDITY_MINUTES = 5;
    private static final int OTP_LENGTH = 6;
    private static final int MAX_OTP_VALUE = 1_000_000;

    private final SecureRandom secureRandom = new SecureRandom();

    public OtpServiceImpl(OtpTokenRepository otpTokenRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.otpTokenRepository = otpTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Hỗ trợ lưu trữ tạm thời raw OTP và OtpToken đã hash.
     * Chỉ được sử dụng nội bộ trong service.
     */
    private record OtpGenerationResult(OtpToken tokenWithHash, String rawOtpCode) {
    }

    /**
     * Helper: Tạo raw OTP, hash nó, và tạo OtpToken (với hash).
     */
    private OtpGenerationResult createAndHashOtpToken(String target, OtpPurpose purpose, OtpChannel channel) {
        String rawOtpCode = generateRandomOtp(); // Raw OTP
        Instant expiresAt = Instant.now().plus(OTP_VALIDITY_MINUTES, ChronoUnit.MINUTES);
        String hashedOtp = passwordEncoder.encode(rawOtpCode); // Hashed OTP

        OtpToken newToken = new OtpToken();
        newToken.setTarget(target);
        newToken.setPurpose(purpose);
        newToken.setOtpHash(hashedOtp); // Lưu HASH vào DB
        newToken.setExpiresAt(expiresAt);
        newToken.setChannel(channel);
        return new OtpGenerationResult(newToken, rawOtpCode);
    }


    private String generateRandomOtp() {
        // Chỉ đọc giá trị hằng số đã được tính toán trước
        int number = secureRandom.nextInt(MAX_OTP_VALUE);
        return String.format("%0" + OTP_LENGTH + "d", number);
    }

    @Override
    @Transactional
    public OtpToken generateOtp(String target, OtpPurpose purpose, OtpChannel channel) {
        // 1. Vô hiệu hóa mã OTP cũ đang hoạt động
        otpTokenRepository.invalidateActiveTokens(target, purpose, Instant.now());

        // 2. Tạo OtpToken mới
        OtpGenerationResult newTokenWithHash = createAndHashOtpToken(target, purpose, channel);

        // 3. Lưu token vào DB
        return otpTokenRepository.save(newTokenWithHash.tokenWithHash);
    }

    @Override
    public boolean sendOtp(OtpToken otpToken, String rawOtpCode) {
        if (otpToken.getChannel() == null) {
            System.err.println("ERROR: Cannot send OTP. Channel is not set on the OtpToken.");
            return false;
        }

        if (otpToken.getChannel() == OtpChannel.EMAIL) {
            // Lấy tên người dùng (giả sử có thể lấy từ một User Service khác)
            String name = "Customer";

            String emailContent = emailService.buildOtpEmailContent(
                    name,
                    rawOtpCode,
                    otpToken.getPurpose().name()
            );

            // Khởi tạo việc gửi mail bất đồng bộ
            emailService.sendEmail(otpToken.getTarget(), "Mã Xác thực OTP", emailContent);
            return true;
        }

        if (otpToken.getChannel() == OtpChannel.SMS) {
            // Logic gửi SMS
            System.out.println("DEBUG: Sending OTP " + rawOtpCode + " to " + otpToken.getTarget() + " via SMS.");
            return true;
        }

        System.err.println("WARNING: Unsupported OTP channel: " + otpToken.getChannel());
        return false;
    }

    @Override
    @Transactional // Phương thức tiện ích phải là transactional
    public OtpToken generateAndSendOtp(String target, OtpPurpose purpose, OtpChannel channel) {
        // 1. Vô hiệu hóa mã OTP cũ đang hoạt động
        otpTokenRepository.invalidateActiveTokens(target, purpose, Instant.now());

        // 2. Tạo OtpToken mới và Hash
        OtpGenerationResult result = createAndHashOtpToken(target, purpose, channel);

        // 3. Lưu token (với Hash) vào DB
        OtpToken savedToken = otpTokenRepository.save(result.tokenWithHash());

        // 4. Gửi token (sử dụng Raw OTP)
        sendOtp(savedToken, result.rawOtpCode()); // UPDATED CALL: Pass raw OTP

        System.out.println("DEBUG: Generated OTP: [" + result.rawOtpCode() + "] - Raw OTP sent to user " + target + " Hashed OTP: " + savedToken.getOtpHash() + "."); // No longer log raw OTP
        return savedToken;
    }

    @Override
    // Sử dụng REQUIRES_NEW để coi như 1 lần validate sẽ trừ đi số lần thử dù đúng hay sai
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean validateOtp(VerifyOtpRequestDTO otpToken) {
        Instant now = Instant.now();
        // 1. Tìm mã OTP hợp lệ nhất (mới nhất, chưa hết hạn, chưa dùng)
        Optional<OtpToken> existingOptionalToken = otpTokenRepository
                .findFirstByTargetAndPurposeAndExpiresAtAfterAndUsedIsFalseOrderByCreatedAtDesc(
                        otpToken.target(), otpToken.purpose(), now
                );
        // Không tìm thấy thì trả về false
        if (existingOptionalToken.isEmpty()) {
            return false;
        }
        // Nếu thấy thì tiếp tục logic
        OtpToken existingToken = existingOptionalToken.get();

        // 2.1 Mã sai: Kiểm tra mã OTP và attemptsLeft
        // Nếu như mã OTP nhập vào không khớp với mã trong DB thì trả về false
        if (!passwordEncoder.matches(otpToken.otp(), existingToken.getOtpHash())) {
            // Trừ đi 1 lần thử OTP này
            existingToken.setAttemptsLeft(existingToken.getAttemptsLeft() - 1);
            // Nếu như mà số lần thử đã hết thì set token đã used = true
            if (existingToken.getAttemptsLeft() <= 0) {
                existingToken.setUsed(true);
            }
            // Lưu lại và trả về false
            otpTokenRepository.save(existingToken);
            return false;
        }

        // 2.2 Mã đúng: Vô hiệu hóa (chống Replay Attack) và xác nhận
        existingToken.setUsed(true);
        existingToken.setAttemptsLeft(0);
        otpTokenRepository.save(existingToken);
        return true;
    }

    @Override
    public int invalidateAllActiveTokens(String target, OtpPurpose purpose) {
        Instant now = Instant.now();
        return otpTokenRepository.invalidateActiveTokens(target, purpose, now);
    }
}
