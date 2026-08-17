package com.cuonglm.ecommerce.backend.location.dto.internal;

/**
 * DistrictInfoView – Projection lấy thông tin Quận/Huyện kèm Province ID cha.
 *
 * @author cuonglmptit
 * @since Monday, 17 August 2026
 */
public interface DistrictInfoView {
    Integer getId();
    String getName();
    ProvinceSummaryInfoView getProvince();

    interface ProvinceSummaryInfoView {
        Integer getId();
    }
}

