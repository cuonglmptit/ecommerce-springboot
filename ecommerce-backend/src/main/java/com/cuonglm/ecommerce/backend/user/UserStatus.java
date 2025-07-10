package com.cuonglm.ecommerce.backend.user;

/**
 * <p>
 * Trạng thái của User
 * </p>
 * Created By: CuongLM - 09/07/2025
 */
public enum UserStatus {
    /**
     * Hoạt động bình thường
     */
    ACTIVE,

    /**
     * Tạm ngừng (user tự deactivate)
     */
    INACTIVE,

    /**
     * Bị khóa tạm thời
     */
    SUSPENDED,

    /**
     * Bị ban vĩnh viễn
     */
    BANNED
}
