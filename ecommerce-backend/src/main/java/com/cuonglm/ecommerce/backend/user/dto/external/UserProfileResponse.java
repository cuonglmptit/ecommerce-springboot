package com.cuonglm.ecommerce.backend.user.dto.external;

import com.cuonglm.ecommerce.backend.user.dto.internal.UserProfileInfoView;
import com.cuonglm.ecommerce.backend.user.entity.User;
import com.cuonglm.ecommerce.backend.user.enums.Gender;
import com.cuonglm.ecommerce.backend.user.enums.UserRole;
import com.cuonglm.ecommerce.backend.user.enums.UserStatus;

import java.time.LocalDate;
import java.util.Set;

/**
 * UserProfileResponse – DTO trả về toàn bộ thông tin cá nhân (Profile) cho người dùng.
 *
 * @author cuonglmptit
 * @since Tuesday, 18 August 2026
 */
public record UserProfileResponse(
        Long id,
        String username,
        String email,
        String phoneNumber,
        String fullName,
        String avatarUrl,
        LocalDate dateOfBirth,
        Gender gender,
        boolean isEmailVerified,
        boolean isPhoneVerified,
        boolean isLocalPasswordSet,
        UserStatus status,
        Set<UserRole> roles
) {
    public static UserProfileResponse fromEntity(User user) {
        if (user == null) return null;
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getFullName(),
                user.getAvatarUrl(),
                user.getDateOfBirth(),
                user.getGender(),
                user.isEmailVerified(),
                user.isPhoneVerified(),
                user.isLocalPasswordSet(),
                user.getStatus(),
                user.getRoles() != null ? user.getRoles() : Set.of()
        );
    }

    public static UserProfileResponse fromView(UserProfileInfoView view) {
        if (view == null) return null;
        return new UserProfileResponse(
                view.getId(),
                view.getUsername(),
                view.getEmail(),
                view.getPhoneNumber(),
                view.getFullName(),
                view.getAvatarUrl(),
                view.getDateOfBirth(),
                view.getGender(),
                view.isEmailVerified(),
                view.isPhoneVerified(),
                view.isLocalPasswordSet(),
                view.getStatus(),
                view.getRoles() != null ? view.getRoles() : Set.of()
        );
    }
}