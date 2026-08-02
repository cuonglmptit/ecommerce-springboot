package com.cuonglm.ecommerce.backend.attribute.service;

import com.cuonglm.ecommerce.backend.attribute.dto.external.AttributeCreateRequestDTO;
import com.cuonglm.ecommerce.backend.attribute.dto.external.AttributeCreateResponseDTO;
import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeInfoDTO;
import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeOptionInfoDTO;
import com.cuonglm.ecommerce.backend.attribute.entity.Attribute;
import com.cuonglm.ecommerce.backend.attribute.entity.AttributeOption;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * AttributeService – Interface mô tả các method cho {@link Attribute};
 *
 * @author cuonglmptit
 * @since Saturday, 29 November 2025
 */
public interface AttributeService {

    Attribute getAttributeReference(UUID id);
    AttributeOption getAttributeOptionReference(UUID id);

    /**
     * Tạo một Attribute mới
     * @param attrDTO DTO request để tạo Attribute
     * @return Thông tin viẹc tạo Attribute trong {@link AttributeCreateResponseDTO}
     */
    AttributeCreateResponseDTO createAttribute(AttributeCreateRequestDTO attrDTO);

    /**
     * Tìm thông tin về một {@link Attribute} được trả về qua {@link AttributeInfoDTO}
     *
     * @param uuid id của {@link Attribute}
     * @return Một {@link AttributeInfoDTO} chứa thông tin hoặc {@link Optional#empty()}
     */
    Optional<AttributeInfoDTO> findAttributeInfoById(UUID uuid);

    /**
     * Tìm thông tin về một {@link Attribute} được trả về qua {@link AttributeOptionInfoDTO}
     *
     * @param uuid id của {@link AttributeOption}
     * @return Một {@link AttributeOptionInfoDTO} chứa thông tin hoặc {@link Optional#empty()}
     */
    Optional<AttributeOptionInfoDTO> findAttributeOptionInfoById(UUID uuid);

    /**
     * Tìm thông tin một list {@link AttributeOptionInfoDTO} bằng các ids
     * @return Danh sách {@link AttributeOptionInfoDTO}
     */
    List<AttributeOptionInfoDTO> findAllAttributeOptionsInfoByIds(List<UUID> ids);

    /**
     * Tìm hoặc tạo {@link AttributeOption} theo thông tin
     * @param shopId id của shop
     * @param attributeName Tên attribute
     * @param optionValue Giá trị của option
     * @return {@link AttributeOption} tìm được hoặc không tìm đc
     */
    AttributeOptionInfoDTO findOrCreateAttributeOption(Long shopId, String attributeName, String optionValue);
}
