package com.cuonglm.ecommerce.backend.user.enums;

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
    DEACTIVATED,

    /**
     * Bị khóa tạm thời
     */
    SUSPENDED,

    /**
     * Bị ban vĩnh viễn
     */
    BANNED,

    /**
     * Đã xóa (soft delete để đảm bảo pháp gì gì đó sau này)
     */
    DELETED,
}
