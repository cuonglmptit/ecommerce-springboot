package com.cuonglm.ecommerce.backend.product.service;

import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeOptionInfoDTO;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeType;
import com.cuonglm.ecommerce.backend.attribute.service.AttributeService;
import com.cuonglm.ecommerce.backend.category.dto.internal.CategoryInfoDTO;
import com.cuonglm.ecommerce.backend.category.service.CategoryService;
import com.cuonglm.ecommerce.backend.core.exception.ConflictException;
import com.cuonglm.ecommerce.backend.core.exception.PermissionDeniedException;
import com.cuonglm.ecommerce.backend.core.exception.ResourceNotFoundException;
import com.cuonglm.ecommerce.backend.core.exception.ValidationErrorsException;
import com.cuonglm.ecommerce.backend.core.pagination.PageResponse;
import com.cuonglm.ecommerce.backend.core.utils.SecurityUtils;
import com.cuonglm.ecommerce.backend.media.dto.internal.MediaInfoDTO;
import com.cuonglm.ecommerce.backend.media.service.MediaService;
import com.cuonglm.ecommerce.backend.product.dto.external.*;
import com.cuonglm.ecommerce.backend.product.entity.Product;
import com.cuonglm.ecommerce.backend.product.entity.ProductVariant;
import com.cuonglm.ecommerce.backend.product.entity.snapshot.ProductMediaSnapshot;
import com.cuonglm.ecommerce.backend.product.entity.snapshot.SpecificationSnapshot;
import com.cuonglm.ecommerce.backend.product.entity.snapshot.VariantAttributeSnapshot;
import com.cuonglm.ecommerce.backend.product.enums.ProductStatus;
import com.cuonglm.ecommerce.backend.product.enums.ProductVariantStatus;
import com.cuonglm.ecommerce.backend.product.projection.ProductDetailInfoView;
import com.cuonglm.ecommerce.backend.product.projection.ProductSummaryInfoView;
import com.cuonglm.ecommerce.backend.product.repository.ProductRepository;
import com.cuonglm.ecommerce.backend.product.repository.ProductVariantRepository;
import com.cuonglm.ecommerce.backend.shop.enums.ShopPermission;
import com.cuonglm.ecommerce.backend.shop.service.ShopService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ProductServiceImpl – Triển khai logic cho {@link ProductService}
 *
 * @author cuonglmptit
 * @since Tuesday, 25 November 2025
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final AttributeService attributeService;
    private final CategoryService categoryService;
    private final ShopService shopService;
    private final MediaService mediaService;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductVariantRepository productVariantRepository,
                              AttributeService attributeService,
                              CategoryService categoryService,
                              ShopService shopService,
                              MediaService mediaService) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.attributeService = attributeService;
        this.categoryService = categoryService;
        this.shopService = shopService;
        this.mediaService = mediaService;
    }

    // ==========================================
    // 1. MUTATION METHODS (GHI)
    // ==========================================

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        // 1. Phân quyền & Identity
        shopService.validatePermission(request.shopId(), ShopPermission.PRODUCT_WRITE);
        Long currentUserId = SecurityUtils.getRequiredCurrentUserId();

        // 2. Validate Category bắt buộc là Leaf node
        CategoryInfoDTO categoryInfo = validateAndGetLeafCategory(request.categoryId());

        // 3. Validate trùng tên sản phẩm trong cùng Shop
        if (productRepository.existsByShopIdAndName(request.shopId(), request.name().trim())) {
            throw new ConflictException("Tên sản phẩm đã tồn tại trong cửa hàng của bạn: " + request.name());
        }

        // 4. Batch Fetch & Validate toàn bộ Media (0 N+1)
        Map<UUID, MediaInfoDTO> mediaMap = fetchAndValidateMediaOwnership(request.media(), request.variants(), currentUserId);

        // 5. In-memory & DB validation cho SKUs
        validateVariantSkus(request.variants(), null);

        // 6. Khởi tạo Aggregate Root Product
        Product product = new Product();
        product.setShop(shopService.getShopReference(request.shopId()));
        product.setCategory(categoryService.getCategoryReference(categoryInfo.id()));
        product.setName(request.name().trim());
        product.setSlug(generateSlug(request.name().trim(), request.shopId()));
        product.setDescription(request.description());
        product.setStatus(ProductStatus.ACTIVE);

        // 7. Đóng gói JSONB Specifications
        if (request.specifications() != null && !request.specifications().isEmpty()) {
            product.setSpecifications(buildSpecificationSnapshots(request.specifications()));
        }

        // 8. Đóng gói JSONB Product Media
        product.setMedia(buildProductMediaSnapshots(request.media(), mediaMap, true));

        // 9. Đóng gói Variants
        for (CreateProductRequest.VariantInput vInput : request.variants()) {
            ProductVariant variant = buildVariant(vInput, request.shopId(), mediaMap);
            product.addVariant(variant);
        }

        // 10. Persist toàn bộ Aggregate trong 1 Transaction
        Product savedProduct = productRepository.save(product);
        return ProductResponse.fromEntity(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        shopService.validatePermission(product.getShop().getId(), ShopPermission.PRODUCT_WRITE);
        Long currentUserId = SecurityUtils.getRequiredCurrentUserId();

        CategoryInfoDTO categoryInfo = validateAndGetLeafCategory(request.categoryId());
        Map<UUID, MediaInfoDTO> mediaMap = fetchAndValidateMediaOwnership(request.media(), request.variants(), currentUserId);
        validateVariantSkus(request.variants(), productId);

        product.setName(request.name().trim());
        product.setCategory(categoryService.getCategoryReference(categoryInfo.id()));
        product.setDescription(request.description());
        if (request.status() != null) {
            product.setStatus(request.status());
        }

        if (request.specifications() != null) {
            product.setSpecifications(buildSpecificationSnapshots(request.specifications()));
        }
        product.setMedia(buildProductMediaSnapshots(request.media(), mediaMap, true));

        // Cập nhật lại Variants: clear cũ và gán mới (OrphanRemoval)
        product.getVariants().clear();
        for (CreateProductRequest.VariantInput vInput : request.variants()) {
            ProductVariant variant = buildVariant(vInput, product.getShop().getId(), mediaMap);
            product.addVariant(variant);
        }

        Product saved = productRepository.save(product);
        return ProductResponse.fromEntity(saved);
    }

    @Override
    public void updateProductStatus(Long productId, UpdateProductStatusRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        shopService.validatePermission(product.getShop().getId(), ShopPermission.PRODUCT_WRITE);
        product.setStatus(request.status());
        productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        shopService.validatePermission(product.getShop().getId(), ShopPermission.PRODUCT_WRITE);
        product.setStatus(ProductStatus.DELETED);
        productRepository.save(product);
    }

    // ==========================================
    // 2. QUERY METHODS (ĐỌC)
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductDetail(Long productId) {
        ProductDetailInfoView view = productRepository.findDetailInfoById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        return ProductResponse.fromView(view);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getPublicProductDetail(Long productId) {
        ProductDetailInfoView view = productRepository.findDetailInfoByIdAndStatus(productId, ProductStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại hoặc đã ngừng kinh doanh."));
        return ProductResponse.fromView(view);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> searchPublicProducts(ProductSearchFilterRequest filter, Pageable pageable) {
        String keyword = (filter != null && filter.keyword() != null && !filter.keyword().isBlank()) ? filter.keyword().trim() : null;
        Long categoryId = filter != null ? filter.categoryId() : null;
        Long shopId = filter != null ? filter.shopId() : null;

        Page<ProductSummaryInfoView> page = productRepository.searchPublicProducts(keyword, categoryId, shopId, pageable);
        return PageResponse.from(page.map(ProductSummaryResponse::fromView));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> getShopProducts(Long shopId, ProductSearchFilterRequest filter, Pageable pageable) {
        shopService.validatePermission(shopId, ShopPermission.PRODUCT_READ);

        String keyword = (filter != null && filter.keyword() != null && !filter.keyword().isBlank()) ? filter.keyword().trim() : null;
        ProductStatus status = filter != null ? filter.status() : null;
        Long categoryId = filter != null ? filter.categoryId() : null;

        Page<ProductSummaryInfoView> page = productRepository.searchShopProducts(shopId, keyword, status, categoryId, pageable);
        return PageResponse.from(page.map(ProductSummaryResponse::fromView));
    }

    // ==========================================
    // 3. PRIVATE HELPER METHODS
    // ==========================================

    private CategoryInfoDTO validateAndGetLeafCategory(Long categoryId) {
        CategoryInfoDTO category = categoryService.findCategoryInfoById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));

        if (category.hasChildren()) {
            throw new ValidationErrorsException("Danh mục được chọn phải là danh mục lá (không có danh mục con).",
                    Map.of("categoryId", "Danh mục này chứa các danh mục con. Vui lòng chọn danh mục cụ thể hơn."));
        }
        return category;
    }

    private void validateVariantSkus(List<CreateProductRequest.VariantInput> variants, Long currentProductId) {
        Set<String> requestSkus = new HashSet<>();
        for (CreateProductRequest.VariantInput v : variants) {
            String sku = v.sku().trim().toUpperCase();
            if (!requestSkus.add(sku)) {
                throw new ConflictException("Mã SKU bị trùng lặp trong danh sách biến thể gửi lên: " + sku);
            }

            if (v.salePrice() != null && v.salePrice().compareTo(v.price()) > 0) {
                throw new ValidationErrorsException("Giá khuyến mãi không thể lớn hơn giá gốc",
                        Map.of("salePrice", "Sale price [" + v.salePrice() + "] > regular price [" + v.price() + "]"));
            }

            productVariantRepository.findBySku(sku).ifPresent(existingVariant -> {
                if (currentProductId == null || !existingVariant.getProduct().getId().equals(currentProductId)) {
                    throw new ConflictException("Mã SKU đã tồn tại trong hệ thống: " + sku);
                }
            });
        }
    }

    private Map<UUID, MediaInfoDTO> fetchAndValidateMediaOwnership(List<CreateProductRequest.MediaInput> productMedia,
                                                                   List<CreateProductRequest.VariantInput> variants,
                                                                   Long currentUserId) {
        Set<UUID> allMediaIds = new HashSet<>();
        if (productMedia != null) {
            productMedia.forEach(m -> allMediaIds.add(m.mediaId()));
        }
        if (variants != null) {
            variants.stream()
                    .filter(v -> v.media() != null)
                    .flatMap(v -> v.media().stream())
                    .forEach(m -> allMediaIds.add(m.mediaId()));
        }

        if (allMediaIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // GỌI ĐÚNG TÊN METHOD TRONG MediaService: findMediaInfoByIds
        List<MediaInfoDTO> mediaList = mediaService.findMediaInfoByIds(allMediaIds);
        Map<UUID, MediaInfoDTO> mediaMap = mediaList.stream()
                .collect(Collectors.toMap(MediaInfoDTO::id, Function.identity()));

        for (UUID mediaId : allMediaIds) {
            MediaInfoDTO info = mediaMap.get(mediaId);
            if (info == null) {
                throw new ResourceNotFoundException("Media not found: " + mediaId);
            }
            if (!info.uploaderId().equals(currentUserId)) {
                throw new PermissionDeniedException("Media " + mediaId + " không thuộc quyền sở hữu của bạn.");
            }
        }
        return mediaMap;
    }

    private ProductVariant buildVariant(CreateProductRequest.VariantInput input,
                                        Long shopId,
                                        Map<UUID, MediaInfoDTO> mediaMap) {
        ProductVariant variant = new ProductVariant();
        variant.setSku(input.sku().trim().toUpperCase());
        variant.setPrice(input.price());
        variant.setSalePrice(input.salePrice());
        variant.setStockQuantity(input.stockQuantity());
        variant.setStatus(ProductVariantStatus.ACTIVE);

        if (input.attributes() != null && !input.attributes().isEmpty()) {
            Set<UUID> seenAttributeIds = new HashSet<>();
            List<VariantAttributeSnapshot> attrSnapshots = new ArrayList<>();

            for (CreateProductRequest.VariantAttrInput attrInput : input.attributes()) {
                AttributeOptionInfoDTO option;
                if (attrInput.optionId() != null) {
                    option = attributeService.findAttributeOptionInfoById(attrInput.optionId())
                            .orElseThrow(() -> new ResourceNotFoundException("Attribute Option", attrInput.optionId()));
                } else {
                    option = attributeService.findOrCreateAttributeOption(
                            shopId,
                            attrInput.attributeName().trim(),
                            attrInput.optionValue().trim(),
                            AttributeType.VARIATION
                    );
                }

                if (!seenAttributeIds.add(option.attributeId())) {
                    throw new ConflictException("Biến thể chứa thuộc tính trùng lặp: " + option.attributeName());
                }

                boolean isGlobal = option.shopId() == null;
                boolean isOwned = option.shopId() != null && option.shopId().equals(shopId);
                if (!isGlobal && !isOwned) {
                    throw new PermissionDeniedException("Không có quyền sử dụng Option: " + option.id());
                }

                attrSnapshots.add(VariantAttributeSnapshot.fromInfo(option));
            }
            variant.setAttributes(attrSnapshots);
        }

        if (input.media() != null && !input.media().isEmpty()) {
            variant.setMedia(buildProductMediaSnapshots(input.media(), mediaMap, false));
        }

        return variant;
    }

    private List<ProductMediaSnapshot> buildProductMediaSnapshots(List<CreateProductRequest.MediaInput> mediaInputs,
                                                                  Map<UUID, MediaInfoDTO> mediaMap,
                                                                  boolean isProductMedia) {
        if (mediaInputs == null || mediaInputs.isEmpty()) {
            return Collections.emptyList();
        }

        boolean hasThumbnail = mediaInputs.stream().anyMatch(CreateProductRequest.MediaInput::isThumbnail);

        return mediaInputs.stream()
                .map(m -> {
                    MediaInfoDTO info = mediaMap.get(m.mediaId());
                    boolean isThumb = isProductMedia && ((!hasThumbnail && m.sortOrder() == 0) || m.isThumbnail());
                    return ProductMediaSnapshot.of(m.mediaId(), info.url(), m.alt(), isThumb, m.sortOrder());
                })
                .toList();
    }

    private List<SpecificationSnapshot> buildSpecificationSnapshots(List<CreateProductRequest.SpecInput> specs) {
        return specs.stream()
                .map(s -> SpecificationSnapshot.of(
                        s.attributeId(),
                        s.attributeName().trim(),
                        s.attributeCode(),
                        s.optionId(),
                        s.optionValue() != null ? s.optionValue().trim() : null,
                        s.rawValue() != null ? s.rawValue().trim() : null
                ))
                .toList();
    }

    private String generateSlug(String name, Long shopId) {
        String nowhitespace = WHITESPACE.matcher(name).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("").toLowerCase(Locale.ENGLISH);

        String candidate = slug;
        if (productRepository.existsByShopIdAndSlug(shopId, candidate)) {
            candidate = slug + "-" + UUID.randomUUID().toString().substring(0, 6);
        }
        return candidate;
    }
}