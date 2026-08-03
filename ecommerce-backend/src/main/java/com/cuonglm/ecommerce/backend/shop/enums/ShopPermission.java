package com.cuonglm.ecommerce.backend.shop.enums;

/**
 * ShopPermission – Quyền thao tác shop
 *
 * <p>
 * Các quyền được phép khi thao tác với shop
 * </p>
 *
 * @author cuonglmptit
 * @since Monday, 03 August 2026
 */
public enum ShopPermission {
    ATTRIBUTE_READ,
    ATTRIBUTE_WRITE,

    PRODUCT_READ,
    PRODUCT_WRITE,

    ORDER_READ,
    ORDER_WRITE,

    FINANCE_READ,

    SHOP_SETTINGS_WRITE,
    MANAGE_STAFF
}
