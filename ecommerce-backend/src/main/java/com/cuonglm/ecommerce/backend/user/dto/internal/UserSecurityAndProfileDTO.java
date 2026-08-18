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
public record UserSecurityAndProfileDTO(
        Long id,
        String username,
        String passwordHash,
        Set<UserRole> authorities,
        UserStatus status,
        String email,
        String avatarUrl,
        String fullName,
        boolean isEmailVerified
) {
    public static UserSecurityAndProfileDTO fromEntity(User user) {
        if (user == null) return null;
        return new UserSecurityAndProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getRoles() != null ? user.getRoles() : Set.of(),
                user.getStatus(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getFullName(),
                user.isEmailVerified()
        );
    }
}