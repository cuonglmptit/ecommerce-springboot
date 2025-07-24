package com.cuonglm.ecommerce.backend.media;

/**
 * MediaProvider – Enum xác định nguồn lưu trữ hoặc cung cấp media (ảnh, video...).
 *
 * <p>
 * Được sử dụng để chỉ ra media được lưu trữ hoặc truyền tải từ dịch vụ nào,
 * chẳng hạn như Cloudinary cho ảnh, YouTube cho video, hoặc lưu trữ nội bộ (LOCAL).
 * Mỗi provider có cách thức xử lý và API khác nhau để upload, delete và serve media nên cần Enum này check logic.
 * </p>
 *
 * @author cuonglmptit
 * @since Thursday, 24 July 2025
 */
public enum MediaProvider {
    LOCAL,
    CLOUDINARY,
    YOUTUBE,
    VIMEO,
    AWS_S3,
    IMGUR
}
