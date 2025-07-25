package com.cuonglm.ecommerce.backend.user.enums;

/**
 * UserAddressType – Các loại địa chỉ mà người dùng có thể lưu.
 *
 * <p>Dùng cho phân loại địa chỉ trong quá trình mua hàng, giao hàng.</p>
 *
 * @author cuonglmptit
 * @since Friday, 25 July 2025
 */
public enum UserAddressType {
    HOME,
    WORK,
    SHIPPING,
    BILLING,
    TEMPORARY,
    GIFT,
    OTHER;
}
