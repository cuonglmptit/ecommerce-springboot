package com.cuonglm.ecommerce.backend.location.dto.internal;

/**
 * WardInfoDTO – DTO trao đổi thông tin Phường/Xã nội bộ.
 *
 * @author cuonglmptit
 * @since Sunday, 16 August 2026
 */
public record WardInfoDTO(
        Integer id,
        String name,
        Integer districtId
) {
    public static WardInfoDTO fromView(WardInfoView view) {
        if (view == null) return null;
        Integer distId = (view.getDistrict() != null) ? view.getDistrict().getId() : null;
        return new WardInfoDTO(view.getId(), view.getName(), distId);
    }
}