package com.cuonglm.ecommerce.backend.product.dto.external;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

/**
 * VariantAttributeInputDTO – Dto nhận thông tin thuộc tính biến thể
 *
 * @author cuonglmptit
 * @since Sunday, 02 August 2026
 */
public record VariantAttributeInputDTO(
        UUID attributeId,
        @NotBlank(message = "Tên thuộc tính không được để trống")
        String attributeName,  // VD: "Màu sắc"

        UUID optionId,
        @NotBlank(message = "Giá trị thuộc tính không được để trống")
        String optionValue     // VD: "Tím Mộng Mơ"
) {
}