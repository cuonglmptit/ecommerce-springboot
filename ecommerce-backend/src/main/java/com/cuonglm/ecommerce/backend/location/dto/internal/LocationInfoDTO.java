package com.cuonglm.ecommerce.backend.location.dto.internal;

import com.cuonglm.ecommerce.backend.location.entity.Location;

import java.math.BigDecimal;

/**
 * LocationInfoDTO – DTO cung cấp thông tin địa chỉ chi tiết cho User, Shop, Order.
 *
 * @author cuonglmptit
 * @since Sunday, 16 August 2026
 */
public record LocationInfoDTO(
        Long id,
        Integer provinceId,
        String provinceName,
        Integer districtId,
        String districtName,
        Integer wardId,
        String wardName,
        String addressLine,
        String fullAddress,
        BigDecimal latitude,
        BigDecimal longitude,
        String formattedAddress,
        String placeId
) {
    public static LocationInfoDTO fromEntity(Location location) {
        if (location == null) return null;

        String pName = (location.getProvince() != null) ? location.getProvince().getName() : "";
        String dName = (location.getDistrict() != null) ? location.getDistrict().getName() : "";
        String wName = (location.getWard() != null) ? location.getWard().getName() : "";
        String addr = (location.getAddressLine() != null) ? location.getAddressLine() : "";

        // Ghép chuỗi địa chỉ đầy đủ: "Số nhà..., Xã..., Huyện..., Tỉnh..."
        String fullAddr = String.format("%s, %s, %s, %s", addr, wName, dName, pName)
                .replaceAll("^[\\s,]+|[\\s,]+$", "")
                .replaceAll(",\\s*,", ", ");

        return new LocationInfoDTO(
                location.getId(),
                location.getProvince() != null ? location.getProvince().getId() : null,
                pName,
                location.getDistrict() != null ? location.getDistrict().getId() : null,
                dName,
                location.getWard() != null ? location.getWard().getId() : null,
                wName,
                addr,
                fullAddr,
                location.getLatitude(),
                location.getLongitude(),
                location.getFormattedAddress(),
                location.getPlaceId()
        );
    }
}
