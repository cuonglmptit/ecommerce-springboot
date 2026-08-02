package com.cuonglm.ecommerce.backend.attribute.dto.external;

import jakarta.validation.Valid;
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
public record AttributeOptionAttachRequestDTO(
        @NotNull(message = "Phải có id của Attribute để gắn!")
        UUID attributeId, // Bắt buộc: id của Attribute cha đã có (ví dụ: id của "Màu sắc")

        @Valid
        @Size(min = 1, message = "Phải có ít nhất một Option để thêm.")
        List<AttributeOptionCreateRequestDTO> newOptions // Danh sách các Option mới muốn thêm (ví dụ: "Màu Vàng")
) {
}