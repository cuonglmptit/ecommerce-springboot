package com.cuonglm.ecommerce.backend.location.dto.internal;

/**
 * ProvinceInfoDTO – DTO trao đổi thông tin Tỉnh/Thành phố nội bộ.
 *
 * @author cuonglmptit
 * @since Sunday, 16 August 2026
 */
public record ProvinceInfoDTO(
        Integer id,
        String name
) {
    public static ProvinceInfoDTO fromView(ProvinceInfoView view) {
        if (view == null) return null;
        return new ProvinceInfoDTO(view.getId(), view.getName());
    }
}