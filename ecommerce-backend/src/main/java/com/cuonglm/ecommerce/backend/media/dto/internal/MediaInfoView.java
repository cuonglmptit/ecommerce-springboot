package com.cuonglm.ecommerce.backend.media.dto.internal;

import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
import com.cuonglm.ecommerce.backend.media.enums.MediaType;

import java.util.UUID;

/**
 * MediaInfoView - Interface để sử dụng cho projection lấy ra MediaInfo.
 *
 * @author cuonglmptit
 * @since Monday, 01 December 2025
 */
public interface MediaInfoView {
    UUID getId();

    MediaType getType();

    BasicStatus getStatus();

    Long getUploader_Id();
}
