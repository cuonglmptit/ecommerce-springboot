package com.cuonglm.ecommerce.backend.attribute.service;

import com.cuonglm.ecommerce.backend.attribute.dto.external.AttributeCreateRequestDTO;
import com.cuonglm.ecommerce.backend.attribute.dto.external.AttributeCreateResponseDTO;
import com.cuonglm.ecommerce.backend.attribute.dto.external.AttributeOptionCreateResponseDTO;
import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeInfoDTO;
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
import com.cuonglm.ecommerce.backend.shop.dto.internal.ShopInfoDTO;
import com.cuonglm.ecommerce.backend.shop.entity.Shop;
import com.cuonglm.ecommerce.backend.shop.service.ShopService;
import com.cuonglm.ecommerce.backend.user.dto.internal.UserInfoDTO;
import com.cuonglm.ecommerce.backend.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public AttributeServiceImpl(AttributeRepository attributeRepository, AttributeOptionRepository attributeOptionRepository, UserService userService, ShopService shopService) {
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
    public AttributeCreateResponseDTO createAttribute(AttributeCreateRequestDTO attrDTO) {
        // 1. Lấy thông tin người dùng và xác định phạm vi (Scope)
        UserInfoDTO currentUser = userService.getCurrentAuthenticatedUserInfo();
        ShopInfoDTO shopInfo = shopService.findShopInfoByOwnerId(currentUser.id())
                .orElseThrow(() -> new ResourceNotFoundException("Không thể tạo Attribute do tài khoản này không sở hữu Shop."));

        // Mặc định: Attribute do người dùng tạo là SHOP-SCOPED
        AttributeScope scope = AttributeScope.SHOP;
        Shop shopRef = shopService.getShopReference(shopInfo.id());

        // 2. Chuẩn hóa và Tạo Code (Ví dụ: "Màu sắc" -> "MAU_SAC")
        String attributeCode = generateAttributeCode(attrDTO.name());

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
                    .map(optionDTO -> {
                        AttributeOption option = new AttributeOption();
                        option.setValue(optionDTO.value());
                        option.setAttribute(savedAttribute);
                        option.setScope(scope);
                        option.setShop(shopRef); // Kế thừa Shop từ Attribute cha
                        option.setStatus(AttributeStatus.ACTIVE);
                        return option;
                    }).toList();

            // Lưu Options trong một Query (tối ưu hơn so với save từng cái)
            savedOptions = attributeOptionRepository.saveAll(newOptions);
        }

        // 7. Map sang Response DTO (Sử dụng stream API)
        List<AttributeOptionCreateResponseDTO> optionResponses = savedOptions.stream()
                .map(opt -> new AttributeOptionCreateResponseDTO(
                        opt.getId(),
                        opt.getValue(),
                        opt.getScope(),
                        opt.getStatus(),
                        opt.getShop() != null ? opt.getShop().getId() : null // Trả về shopId
                )).toList();

        return new AttributeCreateResponseDTO(
                savedAttribute.getId(),
                savedAttribute.getName(),
                savedAttribute.getCode(),
                savedAttribute.getScope(),
                savedAttribute.getStatus(),
                savedAttribute.getShop() != null ? savedAttribute.getShop().getId() : null,
                optionResponses
        );
    }

    @Override
    public AttributeOptionInfoDTO findOrCreateAttributeOption(Long shopId, String attributeName, String optionValue) {
        String cleanAttrName = attributeName.trim();
        String cleanOptValue = optionValue.trim();
        String attributeCode = generateAttributeCode(cleanAttrName);

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
                            newAttr.setType(AttributeType.SPECIFICATION);
                            newAttr.setStatus(AttributeStatus.ACTIVE);
                            return attributeRepository.save(newAttr);
                        }));

        // 2. Tìm AttributeOption theo Attribute và Value (Ignore Case)
        AttributeOption option = attributeOptionRepository.findByAttributeAndValueIgnoreCase(attribute, cleanOptValue)
                .orElseGet(() -> {
                    AttributeOption newOpt = new AttributeOption();
                    newOpt.setAttribute(attribute);
                    newOpt.setValue(cleanOptValue);
                    newOpt.setScope(attribute.getScope());
                    newOpt.setShop(attribute.getShop());
                    newOpt.setStatus(AttributeStatus.ACTIVE);
                    return attributeOptionRepository.save(newOpt);
                });

        return new AttributeOptionInfoDTO(
                option.getId(),
                option.getValue(),
                attribute.getId(),
                attribute.getName(),
                option.getShop() != null ? option.getShop().getId() : null,
                option.getScope(),
                option.getStatus()
        );
    }

    // --- Helper Method ---

    /**
     * Chuyển tên attribute sang code, ví dụ: "Màu Sắc" -> "MAU_SAC"
     *
     * @param name Tên attribute
     * @return Code chuẩn hóa
     */
    private String generateAttributeCode(String name) {
        String normalized = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", ""); // Loại bỏ dấu
        return normalized.toUpperCase()
                .trim()
                .replaceAll("\\s+", "_")
                .replaceAll("[^A-Z0-9_]", "");
    }

    @Override
    public Optional<AttributeInfoDTO> findAttributeInfoById(UUID uuid) {
        return attributeRepository.findAttributeInfoById(uuid)
                .map(
                        view -> new AttributeInfoDTO(
                                view.getId(),
                                view.getName(),
                                view.getCode(),
                                view.getScope(),
                                view.getShopId(),
                                view.getStatus()
                        )
                );
    }

    @Override
    public Optional<AttributeOptionInfoDTO> findAttributeOptionInfoById(UUID uuid) {
        return attributeOptionRepository.findAttributeOptionInfoById(uuid)
                .map(
                        view -> new AttributeOptionInfoDTO(
                                view.getId(),
                                view.getValue(),
                                view.getAttributeId(),
                                view.getAttributeName(),
                                view.getShopId(),
                                view.getScope(),
                                view.getStatus()
                        )
                );
    }

    @Override
    public List<AttributeOptionInfoDTO> findAllAttributeOptionsInfoByIds(List<UUID> ids) {
        return attributeOptionRepository.findAllByIdIn(ids)
                .stream()
                .map(
                        view -> new AttributeOptionInfoDTO(
                                view.getId(),
                                view.getValue(),
                                view.getAttributeId(),
                                view.getAttributeName(),
                                view.getShopId(),
                                view.getScope(),
                                view.getStatus()
                        )
                )
                .collect(Collectors.toList());
    }


}
