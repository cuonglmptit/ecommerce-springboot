package com.cuonglm.ecommerce.backend.product.entity.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

/**
 * ProductMediaSnapshot – Lưu thông tin ảnh/media của sản phẩm hoặc biến thể dưới dạng JSONB.
 *
 * @author cuonglmptit
 * @since Friday, 14 August 2026
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductMediaSnapshot(
        UUID mediaId,
        String url,
        String alt,
        boolean isThumbnail,
        int sortOrder
) {
    public static ProductMediaSnapshot of(UUID mediaId, String url, String alt, boolean isThumbnail, int sortOrder) {
        return new ProductMediaSnapshot(mediaId, url, alt, isThumbnail, sortOrder);
    }

    public static ProductMediaSnapshot of(UUID mediaId, String url, boolean isThumbnail, int sortOrder) {
        return new ProductMediaSnapshot(mediaId, url, null, isThumbnail, sortOrder);
    }
}