package com.cuonglm.ecommerce.backend.product.dto.external;

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
) {}
