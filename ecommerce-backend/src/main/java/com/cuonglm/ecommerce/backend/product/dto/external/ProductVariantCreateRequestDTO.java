package com.cuonglm.ecommerce.backend.product.dto.external;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * ProductVariantRequestDTO – Dto cho việc nhận request tạo một ProductVariant.
 *
 * @author cuonglmptit
 * @since Monday, 24 November 2025
 */
public record ProductVariantCreateRequestDTO(
        @NotBlank(message = "SKU không được để trống")
        @Size(max = 255, message = "SKU không được quá 255 ký tự")
        String sku,

        @NotNull(message = "Giá không được để trống")
        @Min(value = 0, message = "Giá phải lớn hơn 0")
        BigDecimal price,

        @Min(value = 0, message = "Giá khuyến mãi phải lớn hơn hoặc bằng 0")
        BigDecimal salePrice,

        @NotNull(message = "Số lượng tồn kho không được để trống")
        @Min(value = 0, message = "Số lượng tồn kho phải lớn hơn hoặc bằng 0")
        Integer stockQuantity,

        @Valid
        List<VariantAttributeInputDTO> attributes,

        @Valid
        @Size(min = 1, max = 3, message = "Một biến thể chỉ được có tối đa 3 ảnh")
        List<ProductMediaCreateRequestDTO> variantMedia
) {
}