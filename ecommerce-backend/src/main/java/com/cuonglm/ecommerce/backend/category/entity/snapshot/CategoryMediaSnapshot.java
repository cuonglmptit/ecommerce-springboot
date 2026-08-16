package com.cuonglm.ecommerce.backend.category.entity.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

/**
 * CategoryMediaSnapshot – Lưu thông tin media cho Category dưới dạng Jsonb
 *
 * @author cuonglmptit
 * @since Friday, 14 August 2026
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoryMediaSnapshot (
    UUID meidaId,
    String url,
    boolean isThumbnail,
    int sortOrder
){
    public static CategoryMediaSnapshot of(UUID mediaId, String url, boolean isThumbnail, int sortOrder) {
        return new CategoryMediaSnapshot(mediaId, url, isThumbnail, sortOrder);
    }
}
