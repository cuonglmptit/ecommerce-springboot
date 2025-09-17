package com.cuonglm.ecommerce.backend.user.dto.internal;

import com.cuonglm.ecommerce.backend.user.entity.User;
import com.cuonglm.ecommerce.backend.user.enums.UserRole;
import com.cuonglm.ecommerce.backend.user.enums.UserStatus;

import java.util.Set;

/**
 * UserSecurityAndProfileDTO – Lớp để feature auth biết lấy về để implement UserDetails.
 *
 * <p>
 * Lớp này có thể bao gồm các thông tin cơ bản để xác thực và các thông tin bổ sung để issue token.
 * </p>
 *
 * @author cuonglmptit
 * @since Thursday, 14 August 2025
 */
public class UserSecurityAndProfileDTO {
    // username và password là 2 trường bắt buộc của UserDetails
    private Long id;
    private String username;
    private String passwordHash;
    // authorities là danh sách quyền của người dùng
    private Set<UserRole> authorities;
    //status của user
    private UserStatus status;

    // Các thông tin bổ sung khác cho việc issue token
    private String email;
    private String avatarUrl;
    private String fullName;
    private boolean isEmailVerified;

    // constructor để map User entity sang UserSecurityDTO
    public UserSecurityAndProfileDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.passwordHash = user.getPasswordHash();
        this.authorities = user.getRoles();
        this.status = user.getStatus();
        this.email = user.getEmail();
        this.avatarUrl = user.getAvatarUrl();
        this.fullName = user.getFullName();
        this.isEmailVerified = user.isEmailVerified();
    }

    // <editor-fold desc="Getters">
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Set<UserRole> getAuthorities() {
        return authorities;
    }

    public UserStatus getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    // </editor-fold>
    // <editor-fold desc="Setters">
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setAuthorities(Set<UserRole> authorities) {
        this.authorities = authorities;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    // </editor-fold>
}
