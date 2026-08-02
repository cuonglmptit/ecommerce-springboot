package com.cuonglm.ecommerce.backend.product.dto.external;

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
) {}