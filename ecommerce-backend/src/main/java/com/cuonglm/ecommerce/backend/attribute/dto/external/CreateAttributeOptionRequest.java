package com.cuonglm.ecommerce.backend.attribute.dto.external;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * AttributeOptionCreateRequestDTO – DTO cho việc tạo giá trị (Option) của Attribute. *
 *
 * @author cuonglmptit
 * @since Tuesday, 02 December 2025
 */
public record CreateAttributeOptionRequest(
        @NotBlank(message = "Giá trị của tùy chọn thuộc tính không được để trống!")
        @Size(min = 1, max = 255, message = "Giá trị tùy chọn thuộc tính tối đa 255 ký tự!")
        String value
) {
}