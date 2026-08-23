package com.cuonglm.ecommerce.backend.media.projection;

import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
import com.cuonglm.ecommerce.backend.media.enums.MediaScope;
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
    String getUrl();
    MediaType getType();
    MediaScope getScope();
    BasicStatus getStatus();
    AuditView getAudit();

    interface AuditView {
        Long getCreatedById();
    }

    default Long getUploaderId() {
        return getAudit() != null ? getAudit().getCreatedById() : null;
    }
}
