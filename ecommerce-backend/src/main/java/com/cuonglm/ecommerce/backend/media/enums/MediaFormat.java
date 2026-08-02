package com.cuonglm.ecommerce.backend.media.enums;

/**
 * MediaFormat – Định dạng file variantMedia được hỗ trợ.
 *
 * <p>
 * Enum định nghĩa các định dạng file variantMedia được phép upload và xử lý
 * trong hệ thống. Giúp validate file type và xác định cách xử lý phù hợp.
 * </p>
 *
 * @author cuonglmptit
 * @since Thursday, 24 July 2025
 */
public enum MediaFormat {
    // IMAGE FORMATS
    JPG(MediaType.IMAGE),
    PNG(MediaType.IMAGE),
    WEBP(MediaType.IMAGE),
    SVG(MediaType.IMAGE),

    // VIDEO FORMATS
    MP4(MediaType.VIDEO),
    WEBM(MediaType.VIDEO),
    AVI(MediaType.VIDEO),

    // DOCUMENT FORMATS (tương lai sẽ có thể dùng)
    PDF(MediaType.DOCUMENT);

    private final MediaType mediaType;

    // Constructor để gán giá trị
    MediaFormat(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * Phương thức trả về loại variantMedia tương ứng với định dạng.
     */
    public MediaType getMediaType() {
        return mediaType;
    }
}