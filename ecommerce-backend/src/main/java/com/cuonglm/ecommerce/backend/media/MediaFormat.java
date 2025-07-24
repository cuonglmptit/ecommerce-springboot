package com.cuonglm.ecommerce.backend.media;

/**
 * MediaFormat – Định dạng file media được hỗ trợ.
 *
 * <p>
 * Enum định nghĩa các định dạng file media được phép upload và xử lý
 * trong hệ thống. Giúp validate file type và xác định cách xử lý phù hợp.
 * </p>
 *
 * @author cuonglmptit
 * @since Thursday, 24 July 2025
 */
public enum MediaFormat {
    // Image formats
    /**
     * JPEG image format
     */
    JPG,

    /**
     * PNG image format with transparency support
     */
    PNG,

    /**
     * WebP modern image format
     */
    WEBP,

    /**
     * SVG vector image format
     */
    SVG,

    // Video formats
    /**
     * MP4 video format (most common)
     */
    MP4,

    /**
     * WebM video format for web
     */
    WEBM,

    /**
     * AVI video format
     */
    AVI,

    // Document formats (future)
    /**
     * PDF document format
     */
    PDF
}