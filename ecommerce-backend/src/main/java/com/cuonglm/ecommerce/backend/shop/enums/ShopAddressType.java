package com.cuonglm.ecommerce.backend.shop.enums;

/**
 * ShopAddressType – Phân loại mục đích sử dụng của địa chỉ Shop.
 *
 * @author cuonglmptit
 * @since Wednesday, 19 August 2026
 */
public enum ShopAddressType {
    /**
     * Kho lấy hàng (Shipper đến đây lấy hàng khi có đơn mới)
     */
    PICKUP,

    /**
     * Kho trả hàng (Hàng hoàn, hàng bảo hành chuyển về đây)
     */
    RETURN,

    /**
     * Trụ sở chính / Địa chỉ đăng ký kinh doanh
     */
    BUSINESS
}
