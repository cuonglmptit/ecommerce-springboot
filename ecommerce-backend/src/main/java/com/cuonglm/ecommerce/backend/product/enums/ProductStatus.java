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
     * Đang trạng thái nháp
     */
    DRAFT,
    /**
     * Đang chờ duyệt
     */
    PENDING_REVIEW,
    /**
     * Bị khóa do vi phạm
     */
    /**
     * Ngừng kinh doanh: shop chủ động ngưng vĩnh viễn (Không thể mua)
     */
    DISCONTINUED,
    /**
     * Bị khóa: do admin khóa do vi phạm
     */
    SUSPENDED,
    /**
     * Đã soft delete
     */
    DELETED
}
