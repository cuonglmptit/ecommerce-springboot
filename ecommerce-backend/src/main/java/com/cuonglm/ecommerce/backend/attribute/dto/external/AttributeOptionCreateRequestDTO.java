package com.cuonglm.ecommerce.backend.attribute.dto.external;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * AttributeOptionCreateRequestDTO – DTO cho việc tạo giá trị (Option) của Attribute. *
 *
 * @author cuonglmptit
 * @since Tuesday, 02 December 2025
 */
public record AttributeOptionCreateRequestDTO(
        @NotBlank(message = "Giá trị của AttributeOption không được trống!")
        @Size(min = 1, max = 255)
        String value
) {
}