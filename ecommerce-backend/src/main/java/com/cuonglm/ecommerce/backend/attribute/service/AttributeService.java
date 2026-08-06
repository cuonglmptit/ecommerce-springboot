package com.cuonglm.ecommerce.backend.attribute.service;

import com.cuonglm.ecommerce.backend.attribute.dto.external.AttributeCreateRequestDTO;
import com.cuonglm.ecommerce.backend.attribute.dto.external.AttributeCreateResponseDTO;
import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeInfoDTO;
import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeInfoView;
import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeOptionInfoDTO;
import com.cuonglm.ecommerce.backend.attribute.entity.Attribute;
import com.cuonglm.ecommerce.backend.attribute.entity.AttributeOption;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeType;

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
     *
     * @param attrDTO DTO request để tạo Attribute
     * @return Thông tin việc tạo Attribute trong {@link AttributeCreateResponseDTO}
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
     * Tìm thông tin về một {@link AttributeOption} được trả về qua {@link AttributeOptionInfoDTO}
     *
     * @param uuid id của {@link AttributeOption}
     * @return Một {@link AttributeOptionInfoDTO} chứa thông tin hoặc {@link Optional#empty()}
     */
    Optional<AttributeOptionInfoDTO> findAttributeOptionInfoById(UUID uuid);

    /**
     * Tìm thông tin một list {@link AttributeOptionInfoDTO} bằng các ids
     *
     * @return Danh sách {@link AttributeOptionInfoDTO}
     */
    List<AttributeOptionInfoDTO> findAllAttributeOptionsInfoByIds(List<UUID> ids);

    /**
     * Tìm hoặc tạo {@link AttributeOption} theo thông tin
     * * @param shopId        id của shop
     *
     * @param attributeName Tên attribute
     * @param optionValue   Giá trị của option
     * @param attributeType Loai Attribute
     * @return {@link AttributeOption} tìm được hoặc không tìm đc
     */
    AttributeOptionInfoDTO findOrCreateAttributeOption(Long shopId, String attributeName, String optionValue, AttributeType attributeType);

    /**
     * Tìm kiếm và lọc danh sách thuộc tính (Attribute) theo nhiều tiêu chí động.
     *
     * @param shopId   id của shop (truyền null nếu lấy global)
     * @param scopes   từ khóa tìm kiếm
     * @param types    loại thuộc tính (SPECIFICATION / VARIATION)
     * @param statuses các trạng thái cần lọc
     * @param query    từ khóa tìm kiếm
     * @return Danh sách {@link AttributeInfoDTO}
     */
    List<AttributeInfoDTO> searchAttributes(Long shopId,
                                            List<AttributeScope> scopes,
                                            List<AttributeType> types,
                                            List<AttributeStatus> statuses,
                                            String query);

    /**
     * Mặc định lấy status ACTIVE
     */
    List<AttributeInfoDTO> searchAttributes(Long shopId, String query, AttributeType type);

    /**
     * Lấy tất cả thuộc tính mà một Cửa hàng (Shop) có thể sử dụng.
     *
     * @param shopId   ID của cửa hàng cần lấy danh sách thuộc tính khả dụng (Bắt buộc)
     * @param types    Danh sách loại thuộc tính cần lọc (truyền {@code null} nếu lấy tất cả)
     * @param statuses Danh sách trạng thái thuộc tính cần lọc (truyền {@code null} nếu lấy tất cả)
     * @param query    Từ khóa tìm kiếm theo tên thuộc tính (tìm kiếm tương đối, không phân biệt hoa thường)
     * @return Danh sách các đối tượng {@link AttributeInfoView} khả dụng cho shop, sắp xếp theo scope và tên
     */
    List<AttributeInfoDTO> searchUsableShopAttributes(Long shopId,
                                                      List<AttributeType> types,
                                                      List<AttributeStatus> statuses,
                                                      String query);

    /**
     * Mặc định lấy status ACTIVE
     */
    List<AttributeInfoDTO> searchUsableShopAttributes(Long shopId, String query, AttributeType type);

    /**
     * Search các option theo tên attribute
     *
     * @param shopId        id của shop
     * @param attributeName tên attribute
     * @param query         từ khóa tìm kiếm giá trị option
     * @param type          loại thuộc tính
     * @return Danh sách {@link AttributeOptionInfoDTO}
     */
    List<AttributeOptionInfoDTO> searchOptionsByAttributeName(Long shopId,
                                                              String attributeName,
                                                              String query,
                                                              AttributeType type);
}
