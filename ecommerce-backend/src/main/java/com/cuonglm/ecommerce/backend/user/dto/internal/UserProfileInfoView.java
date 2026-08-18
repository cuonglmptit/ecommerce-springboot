package com.cuonglm.ecommerce.backend.user.dto.internal;

import com.cuonglm.ecommerce.backend.user.enums.Gender;
import com.cuonglm.ecommerce.backend.user.enums.UserRole;
import com.cuonglm.ecommerce.backend.user.enums.UserStatus;

import java.time.LocalDate;
import java.util.Set;

/**
 * UserProfileInfoView – Interface Projection đọc dữ liệu Profile của User (Loại bỏ passwordHash).
 *
 * @author cuonglmptit
 * @since Tuesday, 18 August 2026
 */
public interface UserProfileInfoView {
    Long getId();
    String getUsername();
    String getEmail();
    String getPhoneNumber();
    String getFullName();
    String getAvatarUrl();
    LocalDate getDateOfBirth();
    Gender getGender();
    boolean isEmailVerified();
    boolean isPhoneVerified();
    boolean isLocalPasswordSet();
    UserStatus getStatus();
    Set<UserRole> getRoles();
}