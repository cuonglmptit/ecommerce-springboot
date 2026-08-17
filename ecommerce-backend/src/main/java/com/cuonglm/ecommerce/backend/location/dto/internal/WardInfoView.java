package com.cuonglm.ecommerce.backend.location.dto.internal;

/**
 * WardInfoView – Projection lấy thông tin Phường/Xã kèm District ID cha.
 *
 * @author cuonglmptit
 * @since Monday, 17 August 2026
 */
public interface WardInfoView {
    Integer getId();
    String getName();
    DistrictSummaryInfoView getDistrict();

    interface DistrictSummaryInfoView {
        Integer getId();
    }
}