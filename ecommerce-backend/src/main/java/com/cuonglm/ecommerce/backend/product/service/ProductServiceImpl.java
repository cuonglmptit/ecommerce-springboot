package com.cuonglm.ecommerce.backend.product.service;

import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeOptionInfoDTO;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeType;
import com.cuonglm.ecommerce.backend.attribute.service.AttributeService;
import com.cuonglm.ecommerce.backend.category.dto.internal.CategoryInfoDTO;
import com.cuonglm.ecommerce.backend.category.service.CategoryService;
import com.cuonglm.ecommerce.backend.core.exception.ConflictException;
import com.cuonglm.ecommerce.backend.core.exception.PermissionDeniedException;
import com.cuonglm.ecommerce.backend.core.exception.ResourceNotFoundException;
import com.cuonglm.ecommerce.backend.core.utils.SecurityUtils;
import com.cuonglm.ecommerce.backend.media.dto.internal.MediaInfoDTO;
import com.cuonglm.ecommerce.backend.media.service.MediaService;
import com.cuonglm.ecommerce.backend.product.dto.external.*;
import com.cuonglm.ecommerce.backend.product.dto.internal.AttributeSnapshot;
import com.cuonglm.ecommerce.backend.product.entity.Product;
import com.cuonglm.ecommerce.backend.product.entity.ProductMedia;
import com.cuonglm.ecommerce.backend.product.entity.ProductVariant;
import com.cuonglm.ecommerce.backend.product.enums.ProductStatus;
import com.cuonglm.ecommerce.backend.product.enums.ProductVariantStatus;
import com.cuonglm.ecommerce.backend.product.repository.ProductMediaRepository;
import com.cuonglm.ecommerce.backend.product.repository.ProductRepository;
import com.cuonglm.ecommerce.backend.product.repository.ProductVariantRepository;
import com.cuonglm.ecommerce.backend.shop.enums.ShopPermission;
import com.cuonglm.ecommerce.backend.shop.service.ShopService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * ProductServiceImpl – Triển khai logic cho {@link ProductService}
 *
 * @author cuonglmptit
 * @since Tuesday, 25 November 2025
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductMediaRepository productMediaRepository;

    private final AttributeService attributeService;
    private final CategoryService categoryService;
    private final ShopService shopService;
    private final MediaService mediaService;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductVariantRepository productVariantRepository,
                              ProductMediaRepository productMediaRepository,
                              AttributeService attributeService,
                              CategoryService categoryService,
                              ShopService shopService,
                              MediaService mediaService) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.productMediaRepository = productMediaRepository;
        this.attributeService = attributeService;
        this.categoryService = categoryService;
        this.shopService = shopService;
        this.mediaService = mediaService;
    }

    @Override
    public ProductCreateResponseDTO createProduct(ProductCreateRequestDTO request) {
        // 1. Kiểm tra quyền thao tác trên Shop
        // Check User có phải Owner, Admin, hay Nhân viên có quyền PRODUCT_WRITE không.
        shopService.validatePermission(request.shopId(), ShopPermission.PRODUCT_WRITE);

        // Lấy ID người dùng đang thao tác
        Long currentUserId = SecurityUtils.getRequiredCurrentUserId();

        CategoryInfoDTO categoryInfo = categoryService.findCategoryInfoById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Category id: " + request.categoryId()));

        // 2. Tạo & Lưu Product Cha
        Product product = new Product();
        // 👈 Dùng getShopReference trực tiếp từ request.shopId(), không cần query findShopInfoById thừa thãi
        product.setShop(shopService.getShopReference(request.shopId()));
        product.setCategory(categoryService.getCategoryReference(categoryInfo.id()));
        product.setName(request.name());
        product.setDescription(request.description());
        product.setStatus(ProductStatus.ACTIVE);

        Product savedProduct = productRepository.save(product);

        // 3. Xử lý Ảnh chung (Product Media)
        if (request.productMedia() != null) {
            saveProductMedia(savedProduct, null, request.productMedia(), currentUserId);
        }

        // 4. Xử lý Variants (Con)
        if (request.variants() != null) {
            for (ProductVariantCreateRequestDTO variantDTO : request.variants()) {
                createAndSaveVariant(savedProduct, variantDTO, currentUserId);
            }
        }

        // 5. Map Media Response
        List<ProductMediaCreateResponseDTO> mediaResponse = savedProduct.getMedia().stream()
                .map(pm -> new ProductMediaCreateResponseDTO(
                        pm.getId(),
                        pm.getMedia().getUrl(),
                        pm.isThumbnail(),
                        pm.getSortOrder()
                )).toList();

        // 6. Map Variants Response trực tiếp từ List<AttributeSnapshot>
        List<ProductVariantCreateResponseDTO> variantResponse = savedProduct.getVariants().stream()
                .map(v -> new ProductVariantCreateResponseDTO(
                        v.getId(),
                        v.getSku(),
                        v.getPrice(),
                        v.getStockQuantity(),
                        v.getAttributes().stream()
                                .map(attr -> attr.attributeName() + ": " + attr.optionValue())
                                .toList()
                )).toList();

        return new ProductCreateResponseDTO(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getDescription(),
                savedProduct.getStatus().name(),
                mediaResponse,
                variantResponse
        );
    }

    // --- Helper: Lưu Variant bằng JSONB Snapshot ---
    private void createAndSaveVariant(Product product, ProductVariantCreateRequestDTO dto, Long shopOwnerId) {

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku(dto.sku());
        variant.setPrice(dto.price());
        variant.setStockQuantity(dto.stockQuantity());
        variant.setStatus(ProductVariantStatus.ACTIVE);

        // Đóng gói JSONB Attributes
        if (dto.attributes() != null && !dto.attributes().isEmpty()) {
            Set<UUID> seenAttributeIds = new HashSet<>();
            List<AttributeSnapshot> snapshots = new ArrayList<>();
            Long currentShopId = product.getShop().getId();

            for (VariantAttributeInputDTO attrInput : dto.attributes()) {
                AttributeOptionInfoDTO option;

                // Nếu có optionId -> Lấy thông tin từ DB
                if (attrInput.optionId() != null) {
                    option = attributeService.findAttributeOptionInfoById(attrInput.optionId())
                            .orElseThrow(() -> new ResourceNotFoundException("Attribute Option không tồn tại với ID: " + attrInput.optionId()));
                } else {
                    // Nếu optionId == null -> Find-or-Create Atomic
                    option = attributeService.findOrCreateAttributeOption(
                            currentShopId,
                            attrInput.attributeName(),
                            attrInput.optionValue(),
                            AttributeType.VARIATION
                    );
                }

                // Check xem có trùng 2 Option cho cùng 1 Attribute trong 1 Variant không
                if (!seenAttributeIds.add(option.attributeId())) {
                    throw new ConflictException(
                            String.format("Biến thể không hợp lệ. Trùng nhiều hơn 1 giá trị cho thuộc tính: %s - option: %s", option.attributeName(), option.value())
                    );
                }

                // Validate Quyền
                boolean isGlobal = option.shopId() == null;
                boolean isOwned = option.shopId() != null && option.shopId().equals(currentShopId);
                if (!isGlobal && !isOwned) {
                    throw new PermissionDeniedException("Vi phạm quyền sử dụng Attribute Option: " + option.id());
                }

                // Build Snapshot object (Giả định AttributeOptionInfoDTO có trường attributeName)
                snapshots.add(new AttributeSnapshot(
                        option.attributeId(),
                        option.attributeName(),
                        option.id(),
                        option.value()
                ));
            }

            // Gán danh sách snapshot vào Variant
            variant.setAttributes(snapshots);
        } else {
            variant.setAttributes(Collections.emptyList());
        }

        // Lưu Variant (Hibernate tự động serialize `attributes` thành JSONB)
        ProductVariant savedVariant = productVariantRepository.save(variant);

        // Lưu Ảnh riêng của Variant (nếu có)
        if (dto.variantMedia() != null) {
            saveProductMedia(product, savedVariant, dto.variantMedia(), shopOwnerId);
        }
    }

    private void saveProductMedia(Product product, ProductVariant variant,
                                  List<ProductMediaCreateRequestDTO> mediaDTOs,
                                  Long shopOwnerId) {
        List<ProductMedia> mediaList = new ArrayList<>();

        if (product != null && variant == null) {
            if (mediaDTOs.size() < 1 || mediaDTOs.size() > 7)
                throw new IllegalArgumentException("Số lượng ảnh của Product phải từ 1-7");

            List<ProductMediaCreateRequestDTO> thumbnailDTOs = mediaDTOs.stream()
                    .filter(ProductMediaCreateRequestDTO::isThumbnail)
                    .toList();

            if (thumbnailDTOs.isEmpty()) {
                ProductMediaCreateRequestDTO firstMediaUpdated = new ProductMediaCreateRequestDTO(
                        mediaDTOs.get(0).mediaId(),
                        true,
                        mediaDTOs.get(0).sortOrder()
                );
                mediaDTOs.set(0, firstMediaUpdated);
            } else if (thumbnailDTOs.size() > 1) {
                UUID keepThumbnailId = thumbnailDTOs.get(0).mediaId();
                List<ProductMediaCreateRequestDTO> normalizedMediaDTOs = new ArrayList<>();
                for (ProductMediaCreateRequestDTO mediaDTO : mediaDTOs) {
                    boolean newIsThumbnail = mediaDTO.mediaId().equals(keepThumbnailId);
                    ProductMediaCreateRequestDTO newDTO = new ProductMediaCreateRequestDTO(
                            mediaDTO.mediaId(),
                            newIsThumbnail,
                            mediaDTO.sortOrder()
                    );
                    normalizedMediaDTOs.add(newDTO);
                }
                mediaDTOs = normalizedMediaDTOs;
            }
        }

        for (ProductMediaCreateRequestDTO mediaDTO : mediaDTOs) {
            MediaInfoDTO mediaInfo = mediaService.findMediaInfoById(mediaDTO.mediaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Media not found: " + mediaDTO.mediaId()));

            if (!mediaInfo.uploaderId().equals(shopOwnerId)) {
                throw new PermissionDeniedException("Media ID " + mediaDTO.mediaId() + " không thuộc sở hữu của Shop hoặc người upload không phải chủ shop.");
            }

            ProductMedia pm = new ProductMedia();
            pm.setProduct(product);
            pm.setVariant(variant);
            pm.setMedia(mediaService.getMediaReference(mediaInfo.id()));
            pm.setThumbnail(mediaDTO.isThumbnail());
            pm.setSortOrder(mediaDTO.sortOrder());

            mediaList.add(pm);
        }
        productMediaRepository.saveAll(mediaList);
    }
}