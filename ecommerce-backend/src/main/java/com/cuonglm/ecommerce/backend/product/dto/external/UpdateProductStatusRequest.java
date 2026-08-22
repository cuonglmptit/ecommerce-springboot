package com.cuonglm.ecommerce.backend.product.dto.external;

import com.cuonglm.ecommerce.backend.product.enums.ProductStatus;
import jakarta.validation.constraints.NotNull;

/**
 * UpdateProductStatusRequest – Update trạng thái Product
 *
 * @author cuonglmptit
 * @since Saturday, 22 August 2026
 */
public record UpdateProductStatusRequest(
        @NotNull(message = "Trạng thái không được để trống")
        ProductStatus status
) {}