package com.cuonglm.ecommerce.backend.attribute.dto.external;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * AttributeOptionAttachRequestDTO – DTO để thêm/gắn Option mới vào Attribute đã tồn tại.
 *
 * @author cuonglmptit
 * @since Tuesday, 02 December 2025
 */
public record AttachAttributeOptionRequest(
        @NotNull(message = "ID thuộc tính không được để trống!")
        UUID attributeId,

        @NotEmpty(message = "Phải có ít nhất một tùy chọn để thêm mới!")
        @Valid
        List<CreateAttributeOptionRequest> newOptions
) {
}