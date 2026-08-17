package com.cuonglm.ecommerce.backend.location.dto.external;

import java.math.BigDecimal;

/**
 * DistanceCalculationResponse – DTO trả về khoảng cách tính toán được (km).
 *
 * @author cuonglmptit
 * @since Monday, 17 August 2026
 */
public record DistanceCalculationResponse(
        BigDecimal distanceInKm,
        String formattedDistance
) {
    public static DistanceCalculationResponse of(double km) {
        BigDecimal roundedKm = BigDecimal.valueOf(km).setScale(2, java.math.RoundingMode.HALF_UP);
        return new DistanceCalculationResponse(roundedKm, String.format("%.2f km", km));
    }
}