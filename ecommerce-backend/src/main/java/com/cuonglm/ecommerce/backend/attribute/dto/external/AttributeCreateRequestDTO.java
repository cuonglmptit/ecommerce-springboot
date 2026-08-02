package com.cuonglm.ecommerce.backend.attribute.dto.external;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * AttributeCreateRequestDTO – DTO cho việc tạo Attribute.
 *
 * @author cuonglmptit
 * @since Tuesday, 02 December 2025
 */
public record AttributeCreateRequestDTO(
        @NotBlank(message = "Tên thuộc tính không được để trống!")
        @Size(min = 1, max = 100)
        String name, // Ví dụ: "Màu sắc", "Kích thước"

        // Danh sách các Option muốn tạo ngay lập tức
        @Valid
        List<AttributeOptionCreateRequestDTO> initialOptions
) {
}