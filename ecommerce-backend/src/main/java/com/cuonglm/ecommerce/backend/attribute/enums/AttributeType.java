package com.cuonglm.ecommerce.backend.attribute.enums;

/**
 * AttributeType – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 06 February 2026
 */
public enum AttributeType {
    /**
     * Dùng cho thông tin mô tả (VD: Thương hiệu, Chất liệu, Bảo hành).
     * Gắn trực tiếp với Product (1-1).
     */
    SPECIFICATION,
    /**
     * Dùng cho phân loại bán hàng (VD: Màu sắc, Kích cỡ).
     * Gắn với Variant để tạo Matrix (1-n).
     */
    VARIATION
}
