package com.cuonglm.ecommerce.backend.product.dto.external;

import com.cuonglm.ecommerce.backend.product.enums.ProductStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * UpdateProductRequest – DTO update cho Product
 *
 * @author cuonglmptit
 * @since Saturday, 22 August 2026
 */
public record UpdateProductRequest(
        @NotBlank(message = "Tên sản phẩm không được để trống")
        @Size(min = 5, max = 255, message = "Tên sản phẩm phải từ 5 đến 255 ký tự")
        String name,

        @NotNull(message = "Category ID không được để trống")
        Long categoryId,

        @NotBlank(message = "Mô tả sản phẩm không được để trống")
        String description,

        ProductStatus status,

        @Valid
        List<CreateProductRequest.SpecInput> specifications,

        @Valid
        @NotEmpty(message = "Ảnh sản phẩm không được để trống")
        @Size(min = 1, max = 7, message = "Số lượng ảnh của sản phẩm phải từ 1 đến 7")
        List<CreateProductRequest.MediaInput> media,

        @Valid
        @NotEmpty(message = "Sản phẩm phải có ít nhất một biến thể")
        List<CreateProductRequest.VariantInput> variants
) {}