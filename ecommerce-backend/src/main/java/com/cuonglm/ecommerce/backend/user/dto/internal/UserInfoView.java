package com.cuonglm.ecommerce.backend.user.dto.internal;

import com.cuonglm.ecommerce.backend.user.enums.UserRole;
import com.cuonglm.ecommerce.backend.user.enums.UserStatus;

import java.util.Set;

/**
 * UserInfoView – Interface Projection (View) để lấy thông tin User cơ bản.
 *
 * <p>
 * Được sử dụng bởi tầng Repository để chỉ SELECT các cột cần thiết cho việc tạo UserInfoDTO.
 * Giúp tối ưu truy vấn (Lightweight Query) và an toàn Refactoring.
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 21 November 2025
 */
public interface UserInfoView {

    // Các getter phải khớp với tên thuộc tính (hoặc quy tắc JavaBean) trong Entity User
    Long getId();

    String getEmail();

    // Getter cho boolean theo Entity User
    boolean isEmailVerified();

    boolean isPhoneVerified();

    UserStatus getStatus();

    // Spring Data JPA sẽ tự fetch quan hệ ElementCollection (roles) trong Entity User
    Set<UserRole> getRoles();
}