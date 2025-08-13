package com.cuonglm.ecommerce.backend.user.entity;

import com.cuonglm.ecommerce.backend.user.enums.Gender;
import com.cuonglm.ecommerce.backend.user.enums.UserRole;
import com.cuonglm.ecommerce.backend.user.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User – Người dùng hệ thống.
 *
 * <p>
 * Thực thể User của hệ thống
 * </p>
 *
 * @author cuonglmptit
 * @since Sunday, 06 July 2025
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
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    @Size(min = 3, max = 50)
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_\\.]+$")
    private String username;

    /**
     * Created By: cuonglmptit - 19/07/2025
     * <p>
     * Trường mật khẩu dạng hash.
     * </p>
     * <p>
     * Để tối ưu UX thì nếu user đăng nhập bằng oauth2 thì không cần mật khẩu, tuy nhiên logic sẽ tự sinh mật khẩu ngẫu nhiên
     * bằng UUID.randomUUID() để lưu vào trường này.
     * <br>
     * Chỉ khi nào user có thao tác thêm sđt hoặc thay đổi email thì sẽ cần tạo mật khẩu (vì lúc đó user có thể đăng nhập trực tiếp).
     * </p>
     */
    @Column(nullable = false, length = 128)
    private String passwordHash;

    /**
     * Trạng thái mật khẩu cục bộ: true nếu user đã tạo mật khẩu để đăng nhập Form Login.
     * <br>
     * Mặc định: false khi tạo qua OAuth2.
     */
    @Column(nullable = false)
    private boolean isLocalPasswordSet = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role"})
    )
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserAddress> addresses;


    @Column(unique = true)
    private String email;

    @Column(unique = true, nullable = true)
    @Size(min = 10, max = 15)
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    private String avatarUrl;

    @Column(length = 128)
    private String fullName;

    private LocalDate dateOfBirth;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    //07/07/25Phần này chưa xong, sẽ làm thêm audit chuẩn hơn (đang xem xét 2 cách là audit bằng extend/interface hoặc theo spring)
    //08/07/25 thêm tạm, sau này hiểu về spring security và audit thì thực hiện thêm
    @CreatedBy
    @JoinColumn(name = "created_by_id", updatable = false)
    private Long createdById;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedBy
    @JoinColumn(name = "modified_by_id")
    private Long modifiedById;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant modifiedAt;

    @Column(nullable = false)
    private boolean isPhoneVerified = false;
    @Column(nullable = false)
    private boolean isEmailVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    /**
     * Các tài khoản đăng nhập bằng bên thứ 3 (OAuth2)
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserOAuth2Account> oauthAccounts = new HashSet<>();


    //<editor-fold desc="Helper Methods">

    /**
     * Thêm một liên kết
     *
     * @param account
     */
    public void addOAuthAccount(UserOAuth2Account account) {
        if (this.oauthAccounts == null) {
            this.oauthAccounts = new HashSet<>();
        }
        this.oauthAccounts.add(account);
        account.setUser(this);
    }

    public void removeOAuthAccount(UserOAuth2Account account) {
        if (this.oauthAccounts != null) {
            this.oauthAccounts.remove(account);
            // 2. Xử lý mối quan hệ hai chiều (Bidirectional relationship)
            // Việc set account.setUser(null) là cần thiết để ngắt liên kết khóa ngoại.
            account.setUser(null);
        }
    }

    /**
     * Thêm một vai trò mới vào danh sách.
     *
     * @param role Vai trò cần thêm.
     */
    public void addRole(UserRole role) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(role);
    }

    /**
     * Loại bỏ một vai trò cụ thể.
     *
     * @param role Vai trò cần loại bỏ.
     */
    public void removeRole(UserRole role) {
        if (this.roles != null) {
            this.roles.remove(role);
        }
    }
    //</editor-fold>

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

    public List<UserAddress> getAddresses() {
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

    public Long getCreatedBy() {
        return createdById;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Long getModifiedBy() {
        return modifiedById;
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

    public boolean isLocalPasswordSet() {
        return isLocalPasswordSet;
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

    public void setAddresses(List<UserAddress> addresses) {
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

    public void setLocalPasswordSet(boolean localPasswordSet) {
        isLocalPasswordSet = localPasswordSet;
    }

    void lol() {
        // This method is intentionally left blank.
        // It can be used for debugging or testing purposes.
    }
    //</editor-fold>
}
