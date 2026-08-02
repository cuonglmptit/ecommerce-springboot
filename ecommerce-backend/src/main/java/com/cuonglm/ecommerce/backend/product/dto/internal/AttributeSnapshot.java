package com.cuonglm.ecommerce.backend.product.dto.internal;

import java.util.UUID;

/**
 * AttributeSnapshot – Mo_ta_file
 *
 * <p>
 * Mo_ta_chi_tiet
 * </p>
 *
 * @author cuonglmptit
 * @since Saturday, 01 August 2026
 */
public record AttributeSnapshot(
        UUID attributeId,
        String attributeName,
        UUID optionId,
        String optionValue
) {}
