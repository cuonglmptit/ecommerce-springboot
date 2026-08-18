package com.cuonglm.ecommerce.backend.user.entity;

import com.cuonglm.ecommerce.backend.core.entity.AuditMetadata;
import com.cuonglm.ecommerce.backend.user.enums.Gender;
import com.cuonglm.ecommerce.backend.user.enums.UserRole;
import com.cuonglm.ecommerce.backend.user.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.util.ArrayList;
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
    @Pattern(regexp = "^[a-zA-Z0-9_.]+$", message = "Username chỉ được chứa ký tự chữ, số, dấu chấm và gạch dưới")
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserAddress> addresses = new ArrayList<>();


    @Column(unique = true)
    private String email;

    @Column(name = "phone_number", unique = true, nullable = true)
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

    @Column(nullable = false)
    private boolean isPhoneVerified = false;

    @Column(nullable = false)
    private boolean isEmailVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * Các tài khoản đăng nhập bằng bên thứ 3 (OAuth2)
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserOAuth2Account> oauthAccounts = new HashSet<>();

    @Embedded
    private final AuditMetadata audit = new AuditMetadata();


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

    public void addAddress(UserAddress address) {
        if (this.addresses == null) {
            this.addresses = new ArrayList<>();
        }
        this.addresses.add(address);
        address.setUser(this);
    }

    public void removeAddress(UserAddress address) {
        if (this.addresses != null) {
            this.addresses.remove(address);
            address.setUser(null);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Getters/Setters">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isLocalPasswordSet() {
        return isLocalPasswordSet;
    }

    public void setLocalPasswordSet(boolean localPasswordSet) {
        isLocalPasswordSet = localPasswordSet;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    public List<UserAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<UserAddress> addresses) {
        this.addresses = addresses;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public boolean isPhoneVerified() {
        return isPhoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        isPhoneVerified = phoneVerified;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Set<UserOAuth2Account> getOauthAccounts() {
        return oauthAccounts;
    }

    public void setOauthAccounts(Set<UserOAuth2Account> oauthAccounts) {
        this.oauthAccounts = oauthAccounts;
    }

    public AuditMetadata getAudit() {
        return audit;
    }
    //</editor-fold>
}
