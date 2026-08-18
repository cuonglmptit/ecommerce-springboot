package com.cuonglm.ecommerce.backend.user.dto.external;

import com.cuonglm.ecommerce.backend.user.enums.Gender;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * UpdateUserProfileRequest – DTO nhận dữ liệu cập nhật thông tin cá nhân từ Client.
 *
 * @author cuonglmptit
 * @since Tuesday, 18 August 2026
 */
public record UpdateUserProfileRequest(
        @Size(max = 128, message = "Họ và tên không được vượt quá 128 ký tự")
        String fullName,

        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Số điện thoại không hợp lệ")
        String phoneNumber,

        String avatarUrl,

        Gender gender,

        @Past(message = "Ngày sinh phải ở trong quá khứ")
        LocalDate dateOfBirth
) {
}