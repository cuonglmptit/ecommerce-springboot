package com.cuonglm.ecommerce.backend.product.enums;

/**
 * ProductStatus – Trạng thái của sản phẩm
 *
 * <p>
 * Sử dụng để quản lý vòng đời của sản phẩm trong hệ thống.
 * </p>
 *
 * @author cuonglmptit
 * @since Tuesday, 29 July 2025
 */
public enum ProductStatus {
    /**
     * Đang bán, hiển thị public
     */
    ACTIVE,
    /**
     * Ẩn khỏi marketplace nhưng không xóa (do shop tạm tắt)
     */
    INACTIVE,
    /**
     * Hết hàng (có thể vẫn hiển thị)
     */
    OUT_OF_STOCK,
    /**
     * Bị khóa do vi phạm
     */
    SUSPENDED,
    DELETED
}
