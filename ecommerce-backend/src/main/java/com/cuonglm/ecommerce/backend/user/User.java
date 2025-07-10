package com.cuonglm.ecommerce.backend.user;

import com.cuonglm.ecommerce.backend.address.Address;
import com.cuonglm.ecommerce.backend.core.identity.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * <p>User entity</p>
 * Created By: CuongLM - 06/07/2025
 */
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_user_username", columnList = "username"),
                @Index(name = "idx_user_email", columnList = "email"),
                @Index(name = "idx_user_phone", columnList = "phone_number")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    @Size(min = 3, max = 50)
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_\\.]+$")
    private String username;

    @Column(nullable = false, length = 128)
    @NotNull
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role"})
    )
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Address> addresses;


    @Column(unique = true)
    private String email;

    @Column(unique = true, nullable = false)
    @NotNull
    @Size(min = 10, max = 15)
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    private String avatarUrl;

    @Column(length = 128)
    private String fullName;

    private LocalDate dateOfBirth;

    //07/07/25Phần này chưa xong, sẽ làm thêm audit chuẩn hơn (đang xem xét 2 cách là audit bằng extend/interface hoặc theo spring)
    //08/07/25 thêm tạm, sau này hiểu về spring security và audit thì thực hiện thêm
    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", updatable = false)
    private User createdBy;
    @CreatedDate
    private Instant createdAt;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modified_by_id")
    private User modifiedBy;
    @LastModifiedDate
    private Instant modifiedAt;

    @Column(nullable = false)
    private boolean isPhoneVerified = false;
    @Column(nullable = false)
    private boolean isEmailVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    //<editor-fold desc="Getters">
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public User getModifiedBy() {
        return modifiedBy;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public UserStatus getStatus() {
        return status;
    }

    public boolean isPhoneVerified() {
        return isPhoneVerified;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    //</editor-fold>

    //<editor-fold desc="Setters">
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        isPhoneVerified = phoneVerified;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
    //</editor-fold>
}
