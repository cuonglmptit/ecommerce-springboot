package com.cuonglm.ecommerce.backend.user.dto;

import com.cuonglm.ecommerce.backend.user.enums.UserRole;
import com.cuonglm.ecommerce.backend.user.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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