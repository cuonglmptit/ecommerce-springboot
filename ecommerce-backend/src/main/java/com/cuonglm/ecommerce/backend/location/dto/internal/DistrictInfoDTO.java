package com.cuonglm.ecommerce.backend.location.dto.internal;

/**
 * DistrictInfoDTO – DTO trao đổi thông tin Quận/Huyện nội bộ.
 *
 * @author cuonglmptit
 * @since Sunday, 16 August 2026
 */
public record DistrictInfoDTO(
        Integer id,
        String name,
        Integer provinceId
) {
    public static DistrictInfoDTO fromView(DistrictInfoView view) {
        if (view == null) return null;
        Integer provId = (view.getProvince() != null) ? view.getProvince().getId() : null;
        return new DistrictInfoDTO(view.getId(), view.getName(), provId);
    }
}