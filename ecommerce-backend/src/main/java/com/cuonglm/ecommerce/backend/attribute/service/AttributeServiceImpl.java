package com.cuonglm.ecommerce.backend.attribute.service;

import com.cuonglm.ecommerce.backend.attribute.dto.external.AttributeCreateRequestDTO;
import com.cuonglm.ecommerce.backend.attribute.dto.external.AttributeCreateResponseDTO;
import com.cuonglm.ecommerce.backend.attribute.dto.external.AttributeOptionCreateResponseDTO;
import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeInfoDTO;
import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeInfoView;
import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeOptionInfoDTO;
import com.cuonglm.ecommerce.backend.attribute.entity.Attribute;
import com.cuonglm.ecommerce.backend.attribute.entity.AttributeOption;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeType;
import com.cuonglm.ecommerce.backend.attribute.repository.AttributeOptionRepository;
import com.cuonglm.ecommerce.backend.attribute.repository.AttributeRepository;
import com.cuonglm.ecommerce.backend.core.exception.ConflictException;
import com.cuonglm.ecommerce.backend.core.exception.ResourceNotFoundException;
import com.cuonglm.ecommerce.backend.core.utils.NamingUtils;
import com.cuonglm.ecommerce.backend.shop.dto.internal.ShopInfoDTO;
import com.cuonglm.ecommerce.backend.shop.entity.Shop;
import com.cuonglm.ecommerce.backend.shop.enums.ShopPermission;
import com.cuonglm.ecommerce.backend.shop.service.ShopService;
import com.cuonglm.ecommerce.backend.user.dto.internal.UserInfoDTO;
import com.cuonglm.ecommerce.backend.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * AttributeServiceImpl – Triển khai logic cho {@link AttributeService}
 *
 * @author cuonglmptit
 * @since Saturday, 29 November 2025
 */
@Service
@Transactional
public class AttributeServiceImpl implements AttributeService {
    private final AttributeRepository attributeRepository;
    private final AttributeOptionRepository attributeOptionRepository;
    private final UserService userService;
    private final ShopService shopService;

    public AttributeServiceImpl(AttributeRepository attributeRepository,
                                AttributeOptionRepository attributeOptionRepository,
                                UserService userService,
                                ShopService shopService) {
        this.attributeRepository = attributeRepository;
        this.attributeOptionRepository = attributeOptionRepository;
        this.userService = userService;
        this.shopService = shopService;
    }

    @Override
    public Attribute getAttributeReference(UUID id) {
        return attributeRepository.getReferenceById(id);
    }

    @Override
    public AttributeOption getAttributeOptionReference(UUID id) {
        return attributeOptionRepository.getReferenceById(id);
    }


    @Override
    @Transactional(readOnly = true)
    public List<AttributeInfoDTO> searchAttributes(
            Long shopId,
            List<AttributeScope> scopes,
            List<AttributeType> types,
            List<AttributeStatus> statuses,
            String query
    ) {
        if (shopId != null) {
            shopService.validatePermission(shopId, ShopPermission.ATTRIBUTE_READ);
        }

        List<AttributeScope> cleanScopes = (scopes != null && !scopes.isEmpty()) ? scopes : null;
        List<AttributeType> cleanTypes = (types != null && !types.isEmpty()) ? types : null;
        List<AttributeStatus> cleanStatuses = (statuses != null && !statuses.isEmpty()) ? statuses : null;

        return attributeRepository.searchAttributes(
                        shopId, cleanScopes, cleanTypes, cleanStatuses, query)
                .stream()
                .map(AttributeInfoDTO::fromView)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<AttributeInfoDTO> searchAttributes(Long shopId, String query, AttributeType type) {
        List<AttributeType> types = (type != null) ? List.of(type) : null;
        return searchAttributes(shopId, null, types, List.of(AttributeStatus.ACTIVE), query);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeInfoDTO> searchUsableShopAttributes(
            Long shopId,
            List<AttributeType> types,
            List<AttributeStatus> statuses,
            String query
    ) {
        if (shopId != null) {
            shopService.validatePermission(shopId, ShopPermission.ATTRIBUTE_READ);
        }

        List<AttributeType> cleanTypes = (types != null && !types.isEmpty()) ? types : null;
        List<AttributeStatus> cleanStatuses = (statuses != null && !statuses.isEmpty()) ? statuses : null;

        List<AttributeInfoView> views = attributeRepository.searchUsableShopAttributes(
                shopId, cleanTypes, cleanStatuses, query
        );

        return views.stream()
                .map(AttributeInfoDTO::fromView)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<AttributeInfoDTO> searchUsableShopAttributes(Long shopId, String query, AttributeType type) {
        List<AttributeType> types = (type != null) ? List.of(type) : null;
        return searchUsableShopAttributes(shopId, types, List.of(AttributeStatus.ACTIVE), query);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeOptionInfoDTO> searchOptionsByAttributeName(Long shopId, String attributeName, String query, AttributeType type) {
        if (shopId != null) {
            shopService.validatePermission(shopId, ShopPermission.ATTRIBUTE_WRITE);
        }

        List<AttributeType> types = (type != null) ? List.of(type) : null;

        return attributeOptionRepository.searchAttributeOptions(
                        shopId,
                        attributeName,
                        types,
                        List.of(AttributeStatus.ACTIVE),
                        query
                )
                .stream()
                .map(AttributeOptionInfoDTO::fromView)
                .toList();
    }

    @Override
    public AttributeCreateResponseDTO createAttribute(AttributeCreateRequestDTO attrDTO) {
        // 1. Lấy thông tin người dùng và xác định phạm vi (Scope)
        UserInfoDTO currentUser = userService.getCurrentAuthenticatedUserInfo();
        ShopInfoDTO shopInfo = shopService.findShopInfoByOwnerId(currentUser.id())
                .orElseThrow(() -> new ResourceNotFoundException("Không thể tạo Attribute do tài khoản này không sở hữu Shop."));

        // Mặc định: Attribute do người dùng tạo là SHOP-SCOPED
        AttributeScope scope = AttributeScope.SHOP;
        Shop shopRef = shopService.getShopReference(shopInfo.id());

        // 2. Chuẩn hóa và Tạo Code (Ví dụ: "Màu sắc" -> "MAU_SAC")
        String attributeCode = NamingUtils.toConstantName(attrDTO.name());

        // 3. Kiểm tra tính Duy nhất (Unique Constraint)
        // Kiểm tra Code đã tồn tại trong phạm vi Shop này chưa
        if (attributeRepository.existsByShopIdAndCode(shopInfo.id(), attributeCode)) {
            throw new ConflictException("Attribute với tên/code '" + attributeCode + "' đã tồn tại trong Shop này.");
        }

        // 4. Tạo Entity Attribute
        Attribute attribute = new Attribute();
        attribute.setName(attrDTO.name());
        attribute.setCode(attributeCode);
        attribute.setScope(scope);
        attribute.setShop(shopRef);
        attribute.setStatus(AttributeStatus.ACTIVE); // Mặc định ACTIVE

        // 5. Lưu Attribute Cha
        Attribute savedAttribute = attributeRepository.save(attribute);

        // 6. Xử lý và Lưu Attribute Options (Con)
        List<AttributeOption> savedOptions = new ArrayList<>();
        if (attrDTO.initialOptions() != null && !attrDTO.initialOptions().isEmpty()) {
            List<AttributeOption> newOptions = attrDTO.initialOptions().stream()
                    .map(optionDTO -> AttributeOption.of(savedAttribute, optionDTO.value()))
                    .toList();

            // Lưu Options trong một Query (tối ưu hơn so với save từng cái)
            savedOptions = attributeOptionRepository.saveAll(newOptions);
        }

        // 7. Map sang Response DTO (Sử dụng stream API)
        List<AttributeOptionCreateResponseDTO> optionResponses = savedOptions.stream()
                .map(AttributeOptionCreateResponseDTO::fromEntity)
                .toList();

        return AttributeCreateResponseDTO.fromEntity(savedAttribute, optionResponses);
    }

    @Override
    public AttributeOptionInfoDTO findOrCreateAttributeOption(Long shopId, String attributeName, String optionValue, AttributeType attributeType) {
        String cleanAttrName = attributeName.trim();
        String cleanOptValue = optionValue.trim();
        String attributeCode = NamingUtils.toConstantName(cleanAttrName);

        // 1. Tìm Attribute theo ShopID + Code, nếu không có thì tìm Global theo Code, nếu vẫn không có thì tạo mới cho Shop này
        Attribute attribute = attributeRepository.findByShopIdAndCode(shopId, attributeCode)
                .orElseGet(() -> attributeRepository.findByCode(attributeCode)
                        .orElseGet(() -> {
                            Shop shopRef = shopService.getShopReference(shopId);
                            Attribute newAttr = new Attribute();
                            newAttr.setName(cleanAttrName);
                            newAttr.setCode(attributeCode);
                            newAttr.setScope(AttributeScope.SHOP);
                            newAttr.setShop(shopRef);
                            newAttr.setType(attributeType);
                            newAttr.setStatus(AttributeStatus.ACTIVE);
                            return attributeRepository.save(newAttr);
                        }));

        // 2. Tìm AttributeOption theo Attribute và Value (Ignore Case)
        AttributeOption option = attributeOptionRepository.findByAttributeAndValueIgnoreCase(attribute, cleanOptValue)
                .orElseGet(() -> attributeOptionRepository.save(AttributeOption.of(attribute, cleanOptValue)));

        return AttributeOptionInfoDTO.fromEntity(option);
    }

    // --- Helper Method ---

    @Override
    @Transactional(readOnly = true)
    public Optional<AttributeInfoDTO> findAttributeInfoById(UUID uuid) {
        return attributeRepository.findAttributeInfoById(uuid)
                .map(AttributeInfoDTO::fromView);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AttributeOptionInfoDTO> findAttributeOptionInfoById(UUID uuid) {
        return attributeOptionRepository.findAttributeOptionInfoById(uuid)
                .map(AttributeOptionInfoDTO::fromView);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeOptionInfoDTO> findAllAttributeOptionsInfoByIds(List<UUID> ids) {
        return attributeOptionRepository.findAllByIdIn(ids)
                .stream()
                .map(AttributeOptionInfoDTO::fromView)
                .toList();
    }
}
