package com.cuonglm.ecommerce.backend.location.entity;

import com.cuonglm.ecommerce.backend.core.utils.AddressUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * AddressSnapshot – Value Object lưu trữ toàn bộ Snapshot địa chỉ dưới dạng JSONB.
 * Nhúng trực tiếp vào UserAddress, ShopAddress, Order.shippingAddress (0 table join).
 *
 * @author cuonglmptit
 * @since Monday, 17 August 2026
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AddressSnapshot(
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
        String placeId,
        String formattedAddress,
        String countryCode,
        String postalCode
) {
    public static AddressSnapshot of(Integer provinceId, String provinceName,
                                     Integer districtId, String districtName,
                                     Integer wardId, String wardName,
                                     String addressLine,
                                     BigDecimal latitude, BigDecimal longitude,
                                     String placeId, String formattedAddress) {
        String fullAddr = AddressUtils.formatFullAddress(addressLine, wardName, districtName, provinceName);

        return new AddressSnapshot(
                provinceId, provinceName,
                districtId, districtName,
                wardId, wardName,
                addressLine, fullAddr,
                latitude, longitude,
                placeId, formattedAddress,
                "VN",
                null
        );
    }
}