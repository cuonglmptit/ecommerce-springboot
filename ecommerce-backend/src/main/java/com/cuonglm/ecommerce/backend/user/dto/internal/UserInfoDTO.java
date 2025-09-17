package com.cuonglm.ecommerce.backend.user.dto.internal;

import com.cuonglm.ecommerce.backend.user.enums.UserRole;
import com.cuonglm.ecommerce.backend.user.enums.UserStatus;

import java.util.Set;

/**
 * UserInfoDTO – Dto phục vụ cho các service internal của hệ thống.
 *
 * <p>
 * Cung cấp các thông tin cần thiết để service khác có thể thao tác với User.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 19 November 2025
 */
public record UserInfoDTO(
        Long id,
        String email,
        UserStatus status,
        boolean isEmailVerified,
        boolean isPhoneVerified,
        Set<UserRole> roles
) {
    public boolean isAdmin() {
        return this.roles.contains(UserRole.ADMIN);
    }
}