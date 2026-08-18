package com.cuonglm.ecommerce.backend.user.dto.external;

import com.cuonglm.ecommerce.backend.location.dto.external.AddressRequest;
import com.cuonglm.ecommerce.backend.user.enums.UserAddressType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * UpdateUserAddressRequest – DTO nhận dữ liệu chỉnh sửa địa chỉ nhận hàng.
 *
 * @author cuonglmptit
 * @since Tuesday, 18 August 2026
 */
public record UpdateUserAddressRequest(
        @NotBlank(message = "Họ tên người nhận không được để trống")
        @Size(max = 100, message = "Họ tên người nhận không được quá 100 ký tự")
        String fullName,

        @NotBlank(message = "Số điện thoại người nhận không được để trống")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Số điện thoại không hợp lệ")
        String phoneNumber,

        boolean isDefault,

        @NotNull(message = "Loại địa chỉ không được để trống")
        UserAddressType type,

        String note,

        @Valid
        @NotNull(message = "Thông tin địa chỉ không được để trống")
        AddressRequest address
) {
}