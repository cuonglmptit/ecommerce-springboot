package com.cuonglm.ecommerce.backend.user.dto.internal;

import com.cuonglm.ecommerce.backend.user.enums.UserRole;
import com.cuonglm.ecommerce.backend.user.enums.UserStatus;

/**
 * UserCreationDTO – Lớp nhận và chuyển request User giữa client <-> Auth Controller <-> User Service.
 *
 * <p>
 * Lớp này được sử dụng để nhận dữ liệu từ client khi người dùng đăng ký tài khoản mới.
 * </p>
 *
 * @author cuonglmptit
 * @since Thursday, 31 July 2025
 */
public record UserCreationDTO(
        String username,
        String email,
        boolean emailVerified,
        String passwordHash, // **Password đã được hash**
        UserRole initialRole,
        UserStatus userStatus
) {}