package com.cuonglm.ecommerce.backend.user.enums;

/**
 * <p>
 * Role của User
 * </p>
 * Created By: CuongLM - 10/06/2025
 */
public enum UserRole {
    ADMIN,
    SELLER,
    CUSTOMER,
    ;
    public String getName() {
        return name().toLowerCase();
    }
}
