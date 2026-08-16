package com.cuonglm.ecommerce.backend.product.entity.snapshot;

import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeInfoDTO;
import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeOptionInfoDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

/**
 * SpecificationSnapshot – Lưu thông số kỹ thuật (Specs) của Product dưới dạng JSONB.
 *
 * @author cuonglmptit
 * @since Friday, 14 August 2026
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SpecificationSnapshot(
        UUID attributeId,
        String attributeName,
        String attributeCode,
        UUID optionId,
        String optionValue,
        String rawValue
) {
    // Factory method khi chọn Option có sẵn từ dropdown
    public static SpecificationSnapshot fromOption(AttributeOptionInfoDTO option, String attributeCode) {
        if (option == null) return null;
        return new SpecificationSnapshot(
                option.attributeId(),
                option.attributeName(),
                attributeCode,
                option.id(),
                option.value(),
                null
        );
    }

    // Factory method khi nhập giá trị tự do (Free-text / Raw value)
    public static SpecificationSnapshot fromCustomValue(AttributeInfoDTO attribute, String rawValue) {
        if (attribute == null) return null;
        return new SpecificationSnapshot(
                attribute.id(),
                attribute.name(),
                attribute.code(),
                null,
                null,
                rawValue
        );
    }

    // Factory method tổng quát
    public static SpecificationSnapshot of(UUID attributeId, String attributeName, String attributeCode,
                                           UUID optionId, String optionValue, String rawValue) {
        return new SpecificationSnapshot(attributeId, attributeName, attributeCode, optionId, optionValue, rawValue);
    }
}