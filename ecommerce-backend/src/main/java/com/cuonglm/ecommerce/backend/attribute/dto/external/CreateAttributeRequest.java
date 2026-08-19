package com.cuonglm.ecommerce.backend.attribute.dto.external;

import com.cuonglm.ecommerce.backend.attribute.enums.AttributeType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * AttributeCreateRequestDTO – DTO cho việc tạo Attribute.
 *
 * @author cuonglmptit
 * @since Tuesday, 02 December 2025
 */
public record CreateAttributeRequest(
        @NotBlank(message = "Tên thuộc tính không được để trống!")
        @Size(min = 1, max = 100, message = "Tên thuộc tính tối đa 100 ký tự!")
        String name,

        @NotNull(message = "Loại thuộc tính (type) không được để trống!")
        AttributeType type,

        @Valid
        List<CreateAttributeOptionRequest> initialOptions
) {
}