package com.cuonglm.ecommerce.backend.user.dto.internal;

/**
 * UserConflictInfoDTO – DTO chỉ chứa các thông tin cần thiết để kiểm tra xung đột
 * (trùng lặp) username/email trong quá trình đăng ký.
 *
 * <p>
 * DTO này giúp tách biệt miền (Domain Separation) bằng cách ngăn AuthService
 * nhìn thấy Entity User đầy đủ.
 * </p>
 *
 * @param username Tên người dùng.
 * @param email    Địa chỉ email.
 * @author cuonglmptit
 * @since Wednesday, 12 November 2025
 */
public record UserConflictInfoDTO(
        String username,
        String email
) {
}