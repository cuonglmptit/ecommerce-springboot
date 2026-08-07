package com.cuonglm.ecommerce.backend.product.dto.external;

import com.cuonglm.ecommerce.backend.product.entity.Product;

import java.util.List;

/**
 * ProductCreateResponseDTO – Dto trả về cho Product, sử dụng ProductMediaResponseDTO, ProductVariantResponseDTO.
 *
 * @author cuonglmptit
 * @since Thursday, 27 November 2025
 */
public record ProductCreateResponseDTO(
        Long id,
        String name,
        String description,
        String status,
        List<ProductMediaCreateResponseDTO> media,
        List<ProductVariantCreateResponseDTO> variants
) {
    public static ProductCreateResponseDTO fromEntity(Product product) {
        if (product == null) return null;

        List<ProductMediaCreateResponseDTO> mediaDTOs = (product.getMedia() != null) ?
                product.getMedia().stream()
                        .map(ProductMediaCreateResponseDTO::fromEntity)
                        .toList() : List.of();

        List<ProductVariantCreateResponseDTO> variantDTOs = (product.getVariants() != null) ?
                product.getVariants().stream()
                        .map(ProductVariantCreateResponseDTO::fromEntity)
                        .toList() : List.of();

        return new ProductCreateResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getStatus() != null ? product.getStatus().name() : null,
                mediaDTOs,
                variantDTOs
        );
    }
}
