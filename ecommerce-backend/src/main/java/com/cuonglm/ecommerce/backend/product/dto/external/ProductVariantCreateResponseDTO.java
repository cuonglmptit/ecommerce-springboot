package com.cuonglm.ecommerce.backend.product.dto.external;

import com.cuonglm.ecommerce.backend.product.entity.ProductVariant;

import java.math.BigDecimal;
import java.util.List;

/**
 * ProductVariantCreateResponseDTO – Dto trả về cho ProductVariantResponse.
 *
 *
 * @author cuonglmptit
 * @since Thursday, 27 November 2025
 */
public record ProductVariantCreateResponseDTO(
        Long id,
        String sku,
        BigDecimal price,
        int stock,
        List<String> attributes
) {
    public static ProductVariantCreateResponseDTO fromEntity(ProductVariant variant) {
        if (variant == null) return null;

        List<String> attrStrings = (variant.getAttributes() != null) ?
                variant.getAttributes().stream()
                        .map(attr -> attr.attributeName() + ": " + attr.optionValue())
                        .toList() : List.of();

        return new ProductVariantCreateResponseDTO(
                variant.getId(),
                variant.getSku(),
                variant.getPrice(),
                variant.getStockQuantity(),
                attrStrings
        );
    }
}