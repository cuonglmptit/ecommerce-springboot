package com.cuonglm.ecommerce.backend.auth.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    @Override
    public CompletableFuture<Void> sendEmail(String to, String subject, String text) {
        // Sử dụng runAsync để xử lý logic bất đồng bộ và bắt lỗi
        return CompletableFuture.runAsync(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // Chỉ định encoding
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(text, true); // true cho phép gửi nội dung HTML

                mailSender.send(message); // Thao tác chặn I/O

            } catch (Exception e) {
                // Log lỗi chi tiết hơn
                System.err.println("FATAL: Failed to send email to " + to + ". Subject: " + subject + ". Error: " + e.getMessage());
                throw new RuntimeException("Failed to send email", e); // Ném ra một unchecked exception
            }
        });
    }

    @Override
    public String buildOtpEmailContent(String name, String otp, String purpose) {
        // Tên template đơn giản
        String purposeTitle = purpose.replace("_", " ");

        // Template HTML đơn giản, sau này có thể sẽ dùng JTE, FreeMarker, hoặc Thymeleaf,
        // nếu dùng sẽ ưu tiên JTE -> nhanh, nhẹ mới nhất, compile-safety
        String template = String.format("""
                <div style="font-family: Arial, sans-serif; padding: 20px; border: 1px solid #eee;">
                    <p>Hi %s,</p>
                    <p>Mã Xác thực Dùng một lần (OTP) cho mục đích **%s** của bạn là:</p>
                    <h2 style="background: #00466a; color: #fff; padding: 10px; border-radius: 5px; text-align: center; width: max-content;">%s</h2>
                    <p>Mã này sẽ hết hạn sau 5 phút.</p>
                    <p>Trân trọng,<br>Hệ thống Xác thực</p>
                </div>
                """, name, purposeTitle, otp);
        return template;
    }
}
