package com.cuonglm.ecommerce.backend.media;

/**
 * MediaType – Loại media trong hệ thống (ảnh, video, file...).
 *
 * <p>
 * Enum định nghĩa các loại media được hỗ trợ trong hệ thống ecommerce,
 * bao gồm hình ảnh, video và các loại media khác.
 * </p>
 *
 * @author cuonglmptit
 * @since Thursday, 24 July 2025
 */
public enum MediaType {
    /**
     * Hình ảnh (jpg, png, webp, svg)
     */
    IMAGE,

    /**
     * Video (mp4, webm, avi)
     */
    VIDEO,

    /**
     * Thumbnail/ảnh thu nhỏ được tối ưu
     */
    THUMBNAIL,

    /**
     * Icon/biểu tượng nhỏ
     */
    ICON
}