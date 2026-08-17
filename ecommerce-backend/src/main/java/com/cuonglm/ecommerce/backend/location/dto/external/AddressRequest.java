package com.cuonglm.ecommerce.backend.location.dto.external;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * AddressRequest – Record DTO nhận dữ liệu tạo mới Snapshot địa chỉ từ Client.
 *
 * @author cuonglmptit
 * @since Friday, 25 July 2025
 */
public record AddressRequest(
        @NotNull(message = "Tỉnh/Thành phố không được để trống")
        Integer provinceId,

        @NotNull(message = "Quận/Huyện không được để trống")
        Integer districtId,

        @NotNull(message = "Phường/Xã không được để trống")
        Integer wardId,

        @NotBlank(message = "Địa chỉ chi tiết không được để trống")
        String addressLine,

        // Các trường mở rộng (Tùy chọn)
        BigDecimal latitude,
        BigDecimal longitude,
        String formattedAddress,
        String placeId
) {
}