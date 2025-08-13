package com.cuonglm.ecommerce.backend.user.dto;


/**
 * UserCreationResultDTO – DTO chứa kết quả tối thiểu sau khi tạo User thành công.
 *
 * <p>
 * DTO này được sử dụng để trả về cho các Service khác (ví dụ: AuthService) sau khi
 * UserService tạo Entity User, giúp duy trì tính đóng gói và tách biệt miền.
 * </p>
 *
 * @param userId   ID của User vừa được tạo.
 * @param username Tên người dùng.
 * @param email    Địa chỉ email.
 * @author cuonglmptit
 * @since Wednesday, 12 November 2025
 */
public record UserCreationResultDTO(
        Long userId,
        String username,
        String email
) {
}