package com.cuonglm.ecommerce.backend.media.enums;

import java.util.*;
import java.util.stream.Collectors;

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
    JPG(MediaType.IMAGE, "image/jpeg", Set.of("jpg", "jpeg")),
    PNG(MediaType.IMAGE, "image/png", Set.of("png")),
    WEBP(MediaType.IMAGE, "image/webp", Set.of("webp")),
    SVG(MediaType.IMAGE, "image/svg+xml", Set.of("svg")),

    // VIDEO FORMATS
    MP4(MediaType.VIDEO, "video/mp4", Set.of("mp4")),
    WEBM(MediaType.VIDEO, "video/webm", Set.of("webm")),
    AVI(MediaType.VIDEO, "video/x-msvideo", Set.of("avi")),
    MOV(MediaType.VIDEO, "video/quicktime", Set.of("mov")),

    // DOCUMENT FORMATS
    PDF(MediaType.DOCUMENT, "application/pdf", Set.of("pdf"));

    private final MediaType mediaType;
    private final String mimeType;
    private final Set<String> extensions;

    // Bảng tra cứu O(1) trong RAM
    private static final Map<String, MediaFormat> MIME_MAP = new HashMap<>();
    private static final Map<String, MediaFormat> EXT_MAP = new HashMap<>();

    static {
        for (MediaFormat format : values()) {
            MIME_MAP.put(format.mimeType.toLowerCase(), format);
            for (String ext : format.extensions) {
                EXT_MAP.put(ext.toLowerCase(), format);
            }
        }
    }

    MediaFormat(MediaType mediaType, String mimeType, Set<String> extensions) {
        this.mediaType = mediaType;
        this.mimeType = mimeType;
        this.extensions = extensions;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Set<String> getExtensions() {
        return extensions;
    }

    public boolean isImage() {
        return this.mediaType == MediaType.IMAGE;
    }

    public boolean isVideo() {
        return this.mediaType == MediaType.VIDEO;
    }

    /**
     * Tìm MediaFormat từ MIME type (vd: "image/png" -> PNG)
     */
    public static Optional<MediaFormat> fromMimeType(String mimeType) {
        if (mimeType == null || mimeType.isBlank()) return Optional.empty();
        return Optional.ofNullable(MIME_MAP.get(mimeType.trim().toLowerCase()));
    }

    /**
     * Tìm MediaFormat từ đuôi file hoặc định dạng do Cloudinary trả về (vd: "png", "mp4" -> MP4)
     */
    public static Optional<MediaFormat> fromExtension(String extension) {
        if (extension == null || extension.isBlank()) return Optional.empty();
        return Optional.ofNullable(EXT_MAP.get(extension.trim().toLowerCase()));
    }

    /**
     * Trả về danh sách đuôi file được phép dưới dạng chuỗi hiển thị cho người dùng (vd: "JPG, PNG, WEBP, MP4...")
     */
    public static String getAllowedExtensionsDisplay(MediaType... types) {
        Set<MediaType> filterTypes = (types != null && types.length > 0) ? Set.of(types) : EnumSet.allOf(MediaType.class);
        return Arrays.stream(values())
                .filter(f -> filterTypes.contains(f.getMediaType()))
                .flatMap(f -> f.getExtensions().stream())
                .map(String::toUpperCase)
                .collect(Collectors.joining(", "));
    }
}