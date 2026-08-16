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
import com.cuonglm.ecommerce.backend.product.dto.external.CreateProductRequest;
import com.cuonglm.ecommerce.backend.product.dto.external.ProductResponse;
import com.cuonglm.ecommerce.backend.product.entity.Product;
import com.cuonglm.ecommerce.backend.product.entity.ProductVariant;
import com.cuonglm.ecommerce.backend.product.entity.snapshot.ProductMediaSnapshot;
import com.cuonglm.ecommerce.backend.product.entity.snapshot.SpecificationSnapshot;
import com.cuonglm.ecommerce.backend.product.entity.snapshot.VariantAttributeSnapshot;
import com.cuonglm.ecommerce.backend.product.enums.ProductStatus;
import com.cuonglm.ecommerce.backend.product.enums.ProductVariantStatus;
import com.cuonglm.ecommerce.backend.product.repository.ProductRepository;
import com.cuonglm.ecommerce.backend.shop.enums.ShopPermission;
import com.cuonglm.ecommerce.backend.shop.service.ShopService;
import org.springframework.transaction.annotation.Transactional;
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
    private final AttributeService attributeService;
    private final CategoryService categoryService;
    private final ShopService shopService;
    private final MediaService mediaService;

    public ProductServiceImpl(ProductRepository productRepository,
                              AttributeService attributeService,
                              CategoryService categoryService,
                              ShopService shopService,
                              MediaService mediaService) {
        this.productRepository = productRepository;
        this.attributeService = attributeService;
        this.categoryService = categoryService;
        this.shopService = shopService;
        this.mediaService = mediaService;
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        // 1. Kiểm tra quyền thao tác trên Shop
        shopService.validatePermission(request.shopId(), ShopPermission.PRODUCT_WRITE);
        Long currentUserId = SecurityUtils.getRequiredCurrentUserId();

        CategoryInfoDTO categoryInfo = categoryService.findCategoryInfoById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Category id: " + request.categoryId()));

        // 2. Khởi tạo đối tượng Product gốc
        Product product = new Product();
        product.setShop(shopService.getShopReference(request.shopId()));
        product.setCategory(categoryService.getCategoryReference(categoryInfo.id()));
        product.setName(request.name());
        product.setDescription(request.description());
        product.setStatus(ProductStatus.ACTIVE);

        // 3. Đóng gói JSONB Specifications (Thông số kỹ thuật)
        if (request.specifications() != null && !request.specifications().isEmpty()) {
            List<SpecificationSnapshot> specSnapshots = request.specifications().stream()
                    .map(s -> SpecificationSnapshot.of(
                            s.attributeId(),
                            s.attributeName(),
                            s.attributeCode(),
                            s.optionId(),
                            s.optionValue(),
                            s.rawValue()
                    ))
                    .toList();
            product.setSpecifications(specSnapshots);
        }

        // 4. Đóng gói JSONB Product Media
        if (request.media() != null && !request.media().isEmpty()) {
            List<ProductMediaSnapshot> mediaSnapshots = processMediaSnapshots(request.media(), currentUserId, true);
            product.setMedia(mediaSnapshots);
        }

        // 5. Đóng gói Variants (ACID Table + JSONB attributes & media)
        if (request.variants() != null && !request.variants().isEmpty()) {
            for (CreateProductRequest.VariantInput vInput : request.variants()) {
                ProductVariant variant = buildVariant(vInput, request.shopId(), currentUserId);
                product.addVariant(variant);
            }
        }

        // 6. Lưu toàn bộ Product Aggregate trong DUY NHẤT 1 lần save()
        Product savedProduct = productRepository.save(product);

        return ProductResponse.fromEntity(savedProduct);
    }

    // --- Helper: Xử lý và validate Variant ---
    private ProductVariant buildVariant(CreateProductRequest.VariantInput input, Long shopId, Long currentUserId) {
        ProductVariant variant = new ProductVariant();
        variant.setSku(input.sku());
        variant.setPrice(input.price());
        variant.setSalePrice(input.salePrice());
        variant.setStockQuantity(input.stockQuantity());
        variant.setStatus(ProductVariantStatus.ACTIVE);

        // Xử lý JSONB Attributes cho Variant
        if (input.attributes() != null && !input.attributes().isEmpty()) {
            Set<UUID> seenAttributeIds = new HashSet<>();
            List<VariantAttributeSnapshot> attrSnapshots = new ArrayList<>();

            for (CreateProductRequest.VariantAttrInput attrInput : input.attributes()) {
                AttributeOptionInfoDTO option;
                if (attrInput.optionId() != null) {
                    option = attributeService.findAttributeOptionInfoById(attrInput.optionId())
                            .orElseThrow(() -> new ResourceNotFoundException("Attribute Option không tồn tại: " + attrInput.optionId()));
                } else {
                    option = attributeService.findOrCreateAttributeOption(
                            shopId,
                            attrInput.attributeName(),
                            attrInput.optionValue(),
                            AttributeType.VARIATION
                    );
                }

                if (!seenAttributeIds.add(option.attributeId())) {
                    throw new ConflictException("Biến thể không hợp lệ. Trùng thuộc tính: " + option.attributeName());
                }

                boolean isGlobal = option.shopId() == null;
                boolean isOwned = option.shopId() != null && option.shopId().equals(shopId);
                if (!isGlobal && !isOwned) {
                    throw new PermissionDeniedException("Không có quyền sử dụng Attribute Option: " + option.id());
                }

                attrSnapshots.add(VariantAttributeSnapshot.fromInfo(option));
            }
            variant.setAttributes(attrSnapshots);
        }

        // Xử lý JSONB Media cho Variant (nếu có)
        if (input.media() != null && !input.media().isEmpty()) {
            List<ProductMediaSnapshot> variantMediaSnapshots = processMediaSnapshots(input.media(), currentUserId, false);
            variant.setMedia(variantMediaSnapshots);
        }

        return variant;
    }

    // --- Helper: Validate quyền sở hữu Media và đóng gói Snapshot ---
    private List<ProductMediaSnapshot> processMediaSnapshots(List<CreateProductRequest.MediaInput> mediaInputs,
                                                             Long currentUserId,
                                                             boolean isProductMedia) {
        if (isProductMedia && (mediaInputs.size() < 1 || mediaInputs.size() > 7)) {
            throw new IllegalArgumentException("Số lượng ảnh sản phẩm phải từ 1 đến 7");
        }

        // Đảm bảo có ít nhất 1 thumbnail
        boolean hasThumbnail = mediaInputs.stream().anyMatch(CreateProductRequest.MediaInput::isThumbnail);

        return mediaInputs.stream()
                .map(m -> {
                    MediaInfoDTO mediaInfo = mediaService.findMediaInfoById(m.mediaId())
                            .orElseThrow(() -> new ResourceNotFoundException("Media not found: " + m.mediaId()));

                    if (!mediaInfo.uploaderId().equals(currentUserId)) {
                        throw new PermissionDeniedException("Media ID " + m.mediaId() + " không thuộc quyền sở hữu của bạn.");
                    }

                    boolean isThumb = isProductMedia && !hasThumbnail && m.sortOrder() == 0 || m.isThumbnail();
                    return ProductMediaSnapshot.of(m.mediaId(), m.url(), m.alt(), isThumb, m.sortOrder());
                })
                .toList();
    }
}