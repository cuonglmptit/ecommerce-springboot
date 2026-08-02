package com.cuonglm.ecommerce.backend.product.dto.external;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * ProductCreateRequestDTO – Dto cho việc tạo Product, sử dụng ProductVariantCreateRequestDTO, ProductMediaCreateRequestDTO.
 *
 * @author cuonglmptit
 * @since Tuesday, 25 November 2025
 */
public record ProductCreateRequestDTO(
        @NotNull(message = "Shop ID không được để trống")
        Long shopId,

        @NotNull(message = "Category ID không được để trống")
        Long categoryId,

        @NotBlank(message = "Tên sản phẩm không được để trống")
        @Size(min = 5, max = 255, message = "Tên phải từ 5 đến 255 ký tự")
        String name,

        @NotBlank(message = "Mô tả sản phẩm không được để trống")
        String description,

        @Valid
        @NotEmpty(message = "Media của Product không được trống")
        @Size(min = 1, max = 7, message = "Số lượng variantMedia của Product phải từ 1 đến 7")
        List<ProductMediaCreateRequestDTO> productMedia,

        @Valid
        @NotEmpty(message = "Sản phẩm phải có ít nhất một biến thể")
        List<ProductVariantCreateRequestDTO> variants
) {
}
