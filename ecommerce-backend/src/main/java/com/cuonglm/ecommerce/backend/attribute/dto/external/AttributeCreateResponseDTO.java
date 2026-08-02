package com.cuonglm.ecommerce.backend.attribute.dto.external;

import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;

import java.util.List;
import java.util.UUID;

/**
 * AttributeCreateResponseDTO – Dto phục vụ việc tạo trả về kết quả tạo Attribute.
 *
 * @author cuonglmptit
 * @since Tuesday, 02 December 2025
 */
public record AttributeCreateResponseDTO(
        UUID id,
        String name,
        String code, // Code (do hệ thống tạo) là cần thiết cho các service gọi API
        AttributeScope scope,
        AttributeStatus status,
        Long shopId, // ID của Shop sở hữu (null nếu GLOBAL)

        // Danh sách các Option của Attribute này
        List<AttributeOptionCreateResponseDTO> options
) {
}