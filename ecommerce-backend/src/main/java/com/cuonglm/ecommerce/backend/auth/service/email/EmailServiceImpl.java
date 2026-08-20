package com.cuonglm.ecommerce.backend.auth.service.email;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * EmailServiceImpl – Triển khai dịch vụ gửi email sử dụng JavaMailSender.
 *
 * <p>
 * Triển khai các phương thức gửi email thông qua JavaMailSender.
 * Đồng thời  gửi email được thực hiện bất đồng bộ ({@link Async}) để không chặn luồng chính.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 05 November 2025
 */
@Service
public class EmailServiceImpl implements EmailService {

    // JavaMailSender của Spring Boot
    private final JavaMailSender mailSender;
    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    @Override
    public CompletableFuture<Void> sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Failed to send email to {}. Subject: {}. Error: {}", to, subject, e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public String buildOtpEmailContent(String name, String otp, String purpose) {
        // Tên template đơn giản
        String purposeTitle = purpose.replace("_", " ");

        return """
                <div style="font-family: Arial, sans-serif; padding: 24px; border: 1px solid #e2e8f0; border-radius: 8px; max-width: 500px; margin: 0 auto;">
                    <h2 style="color: #0f172a; margin-bottom: 16px;">Xác thực tài khoản</h2>
                    <p style="color: #475569; font-size: 14px;">Xin chào <strong>%s</strong>,</p>
                    <p style="color: #475569; font-size: 14px;">Mã xác thực Dùng một lần (OTP) cho mục đích <strong>%s</strong> của bạn là:</p>
                    <div style="text-align: center; margin: 24px 0;">
                        <span style="display: inline-block; background: #2563eb; color: #ffffff; font-size: 28px; font-weight: bold; letter-spacing: 4px; padding: 12px 24px; border-radius: 6px;">%s</span>
                    </div>
                    <p style="color: #64748b; font-size: 13px;">Mã có hiệu lực trong vòng <strong>5 phút</strong>. Tuyệt đối không chia sẻ mã này với bất kỳ ai.</p>
                    <hr style="border: none; border-top: 1px solid #e2e8f0; margin: 20px 0;" />
                    <p style="color: #94a3b8; font-size: 12px;">Hệ thống Thương mại Điện tử Buytamin.</p>
                </div>
                """.formatted(name, purposeTitle, otp);
    }
}
