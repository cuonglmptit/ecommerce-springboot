package com.cuonglm.ecommerce.backend.auth.entity;

import com.cuonglm.ecommerce.backend.auth.enums.OtpChannel;
import com.cuonglm.ecommerce.backend.auth.enums.OtpPurpose;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * OtpToken - Thực thể chứa mã OTP
 *
 * <p>
 * Phục vụ cho việc tạo, gửi và xác minh OTP.
 * </p>
 *
 * @author cuonglmptit
 * @since Tuesday, 04 November 2025
 */
@Entity
@Table(name = "otp_tokens",
//        uniqueConstraints = @UniqueConstraint(columnNames = {"target", "purpose"}),
        indexes = {
                // Index cho truy vấn tìm mã mới nhất
                @Index(name = "idx_target_purpose_created", columnList = "target, purpose, createdAt"),
                // Index cho trường "expiresAt" (tối ưu cho Cron Job/Cleanup)
                @Index(name = "idx_expires", columnList = "expiresAt"),

        }
)
@EntityListeners(AuditingEntityListener.class)
public class OtpToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // email hoặc phone
    @Column(nullable = false, length = 100)
    private String target;

    /**
     * Kênh mà OTP sẽ được gửi (EMAIL, SMS,...)
     */
    @Enumerated(EnumType.STRING)
    private OtpChannel channel;

    /**
     * Mục đích sử dụng OTP (REGISTER, RESET_PASSWORD, CONFIRM_ORDER,...)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpPurpose purpose;

    /**
     * Value hash của mã OTP
     */
    @Column(nullable = false, length = 60)
    private String otpHash;

    @Column(nullable = false)
    private int attemptsLeft = 5;

    // updatable=false để đảm bảo giá trị không đổi
    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    /**
     * Ngăn ngừa mã OTP được sử dụng lại sau khi đã xác thực thành công
     */
    @Column(nullable = false)
    private boolean used = false;

    //<editor-fold desc="Getters">
    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getTarget() {
        return target;
    }

    public Long getId() {
        return id;
    }

    public OtpChannel getChannel() {
        return channel;
    }

    public OtpPurpose getPurpose() {
        return purpose;
    }

    public String getOtpHash() {
        return otpHash;
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    //</editor-fold>
    // <editor-fold desc="Setters">

    public void setTarget(String target) {
        this.target = target;
    }

    public void setChannel(OtpChannel channel) {
        this.channel = channel;
    }

    public void setPurpose(OtpPurpose purpose) {
        this.purpose = purpose;
    }

    public void setOtpHash(String otpHash) {
        this.otpHash = otpHash;
    }

    public void setAttemptsLeft(int attemptsLeft) {
        this.attemptsLeft = attemptsLeft;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    //</editor-fold>
}
