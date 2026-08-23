package com.cuonglm.ecommerce.backend.media.enums;

/**
 * MediaScope – Quyền truy cập và chính sách lưu trữ của tài nguyên.
 *
 * @author cuonglmptit
 * @since Sunday, 23 August 2026
 */
public enum MediaScope {
    /**
     * Tài nguyên công khai (Sản phẩm, Avatar, Banner, Review...): Bật SHA-256 Deduplication, CDN public.
     */
    PUBLIC,

    /**
     * Tài nguyên bảo mật (KYC, CCCD, Hóa đơn VAT, Khiếu nại...): Cô lập theo User, truy cập qua Signed URL.
     */
    PRIVATE
}