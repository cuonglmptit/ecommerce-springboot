package com.cuonglm.ecommerce.backend.location.dto.external;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DistanceCalculationRequest – DTO nhận 2 cặp tọa độ để tính khoảng cách giao hàng.
 *
 * @author cuonglmptit
 * @since Monday, 17 August 2026
 */
public record DistanceCalculationRequest(
        @NotNull(message = "Vĩ độ điểm xuất phát không được để trống")
        BigDecimal originLat,

        @NotNull(message = "Kinh độ điểm xuất phát không được để trống")
        BigDecimal originLng,

        @NotNull(message = "Vĩ độ điểm đến không được để trống")
        BigDecimal destinationLat,

        @NotNull(message = "Kinh độ điểm đến không được để trống")
        BigDecimal destinationLng
) {
}