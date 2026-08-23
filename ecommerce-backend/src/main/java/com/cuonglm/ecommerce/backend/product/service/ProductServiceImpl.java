package com.cuonglm.ecommerce.backend.product.service;

import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeOptionInfoDTO;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeType;
import com.cuonglm.ecommerce.backend.attribute.service.AttributeService;
import com.cuonglm.ecommerce.backend.category.dto.internal.CategoryInfoDTO;
import com.cuonglm.ecommerce.backend.category.service.CategoryService;
import com.cuonglm.ecommerce.backend.core.exception.PermissionDeniedException;
import com.cuonglm.ecommerce.backend.core.exception.ResourceNotFoundException;
import com.cuonglm.ecommerce.backend.core.exception.ValidationErrorsException;
import com.cuonglm.ecommerce.backend.core.pagination.PageResponse;
import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
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
        // 1. Phân quyền Shop
        shopService.validatePermission(request.shopId(), ShopPermission.PRODUCT_WRITE);

        // 2. Gom toàn bộ lỗi validation
        ValidatedCreationContext context = validateCreationPayload(request);

        // 3. Khởi tạo Aggregate Root Product
        Product product = new Product();
        product.setShop(shopService.getShopReference(request.shopId()));
        product.setCategory(categoryService.getCategoryReference(context.categoryInfo.id()));
        product.setName(request.name().trim());
        product.setSlug(generateSlug(request.name().trim(), request.shopId()));
        product.setDescription(request.description());
        product.setStatus(ProductStatus.ACTIVE);

        // 4. Đóng gói JSONB Specifications
        if (request.specifications() != null && !request.specifications().isEmpty()) {
            product.setSpecifications(buildSpecificationSnapshots(request.specifications()));
        }

        // 5. Đóng gói JSONB Product Media
        product.setMedia(buildProductMediaSnapshots(request.media(), context.mediaMap, true));

        // 6. Đóng gói Variants
        for (CreateProductRequest.VariantInput vInput : request.variants()) {
            ProductVariant variant = buildVariant(vInput, request.shopId(), context.mediaMap);
            product.addVariant(variant);
        }

        // 7. Persist toàn bộ Aggregate trong 1 Transaction
        Product savedProduct = productRepository.save(product);
        return ProductResponse.fromEntity(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        shopService.validatePermission(product.getShop().getId(), ShopPermission.PRODUCT_WRITE);

        // Gom toàn bộ lỗi validation cập nhật
        ValidatedUpdateContext context = validateUpdatePayload(productId, product.getShop().getId(), request);

        product.setName(request.name().trim());
        product.setCategory(categoryService.getCategoryReference(context.categoryInfo.id()));
        product.setDescription(request.description());
        if (request.status() != null) {
            product.setStatus(request.status());
        }

        if (request.specifications() != null) {
            product.setSpecifications(buildSpecificationSnapshots(request.specifications()));
        }
        product.setMedia(buildProductMediaSnapshots(request.media(), context.mediaMap, true));

        // Cập nhật lại Variants
        product.getVariants().clear();
        for (CreateProductRequest.VariantInput vInput : request.variants()) {
            ProductVariant variant = buildVariant(vInput, product.getShop().getId(), context.mediaMap);
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
    // 3. PIPELINE GOM LỖI VALIDATION (ERROR AGGREGATION)
    // ==========================================

    private ValidatedCreationContext validateCreationPayload(CreateProductRequest request) {
        Map<String, Object> errors = new LinkedHashMap<>();

        // 1. Validate Category (Phải là Node Lá)
        CategoryInfoDTO categoryInfo = validateLeafCategory(request.categoryId(), errors);

        // 2. Validate trùng tên sản phẩm trong cùng Shop
        if (request.name() != null && !request.name().trim().isBlank()) {
            if (productRepository.existsByShopIdAndName(request.shopId(), request.name().trim())) {
                errors.put("name", "Tên sản phẩm đã tồn tại trong cửa hàng của bạn: \"" + request.name().trim() + "\"");
            }
        }

        // 3. Batch Validate toàn bộ Media (0 N+1)
        Map<UUID, MediaInfoDTO> mediaMap = fetchAndValidateProductMedia(request.media(), request.variants(), errors);

        // 4. Validate Ma trận SKUs và Biến thể
        validateVariants(request.variants(), null, errors);

        // Ném duy nhất 1 Exception chứa toàn bộ danh sách lỗi
        if (!errors.isEmpty()) {
            throw new ValidationErrorsException("Dữ liệu tạo sản phẩm không hợp lệ.", errors);
        }

        return new ValidatedCreationContext(categoryInfo, mediaMap);
    }

    private ValidatedUpdateContext validateUpdatePayload(Long productId, Long shopId, UpdateProductRequest request) {
        Map<String, Object> errors = new LinkedHashMap<>();

        // 1. Validate Category (Phải là Node Lá)
        CategoryInfoDTO categoryInfo = validateLeafCategory(request.categoryId(), errors);

        // 2. Batch Validate toàn bộ Media (0 N+1)
        Map<UUID, MediaInfoDTO> mediaMap = fetchAndValidateProductMedia(request.media(), request.variants(), errors);

        // 3. Validate Ma trận SKUs và Biến thể
        validateVariants(request.variants(), productId, errors);

        if (!errors.isEmpty()) {
            throw new ValidationErrorsException("Dữ liệu cập nhật sản phẩm không hợp lệ.", errors);
        }

        return new ValidatedUpdateContext(categoryInfo, mediaMap);
    }

    private CategoryInfoDTO validateLeafCategory(Long categoryId, Map<String, Object> errors) {
        if (categoryId == null) {
            errors.put("categoryId", "Ngành hàng không được để trống.");
            return null;
        }
        Optional<CategoryInfoDTO> catOpt = categoryService.findCategoryInfoById(categoryId);
        if (catOpt.isEmpty()) {
            errors.put("categoryId", "Ngành hàng không tồn tại trên hệ thống: ID " + categoryId);
            return null;
        }
        CategoryInfoDTO categoryInfo = catOpt.get();
        if (categoryInfo.hasChildren()) {
            errors.put("categoryId", "Ngành hàng được chọn phải là ngành hàng lá (cấp cuối cùng, không có danh mục con).");
        }
        return categoryInfo;
    }

    private Map<UUID, MediaInfoDTO> fetchAndValidateProductMedia(
            List<CreateProductRequest.MediaInput> productMedia,
            List<CreateProductRequest.VariantInput> variants,
            Map<String, Object> errors
    ) {
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

        List<MediaInfoDTO> mediaList = mediaService.findMediaInfoByIds(allMediaIds);
        Map<UUID, MediaInfoDTO> mediaMap = mediaList.stream()
                .collect(Collectors.toMap(MediaInfoDTO::id, Function.identity()));

        for (UUID mediaId : allMediaIds) {
            MediaInfoDTO info = mediaMap.get(mediaId);
            if (info == null) {
                errors.put("media[" + mediaId + "]", "File ảnh/video không tồn tại trên hệ thống.");
            } else if (info.status() != BasicStatus.ACTIVE) {
                errors.put("media[" + mediaId + "]", "File ảnh/video đã bị vô hiệu hóa hoặc vi phạm chính sách.");
            }
        }

        return mediaMap;
    }

    private void validateVariants(List<CreateProductRequest.VariantInput> variants,
                                  Long currentProductId,
                                  Map<String, Object> errors) {
        if (variants == null || variants.isEmpty()) {
            errors.put("variants", "Sản phẩm phải có ít nhất một biến thể/phân loại.");
            return;
        }

        Set<String> inMemorySkus = new HashSet<>();

        for (int i = 0; i < variants.size(); i++) {
            CreateProductRequest.VariantInput v = variants.get(i);
            String rowKey = "variants[" + i + "]";

            if (v.sku() == null || v.sku().trim().isBlank()) {
                errors.put(rowKey + ".sku", "Mã SKU không được để trống.");
            } else {
                String sku = v.sku().trim().toUpperCase();
                // Kiểm tra trùng lặp trong request gửi lên
                if (!inMemorySkus.add(sku)) {
                    errors.put(rowKey + ".sku", "Mã SKU [" + sku + "] bị trùng với một biến thể khác trong bảng.");
                } else {
                    // Kiểm tra trùng lặp trong Database
                    productVariantRepository.findBySku(sku).ifPresent(existingVariant -> {
                        if (!existingVariant.getProduct().getId().equals(currentProductId)) {
                            errors.put(rowKey + ".sku", "Mã SKU [" + sku + "] đã tồn tại trên hệ thống sàn.");
                        }
                    });
                }
            }

            // Kiểm tra Giá khuyến mãi <= Giá gốc
            if (v.price() != null && v.salePrice() != null) {
                if (v.salePrice().compareTo(v.price()) > 0) {
                    errors.put(rowKey + ".salePrice", String.format("Giá khuyến mãi (%s) không được lớn hơn giá gốc (%s).", v.salePrice(), v.price()));
                }
            }
        }
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

                // Bảo vệ: Không cho phép 1 biến thể có 2 thuộc tính cùng loại (vd: vừa Size S vừa Size M)
                if (!seenAttributeIds.add(option.attributeId())) {
                    throw new ValidationErrorsException("Biến thể chứa thuộc tính bị trùng lặp.",
                            Map.of("attributes", "Thuộc tính [" + option.attributeName() + "] bị khai báo nhiều lần trong 1 biến thể."));
                }

                boolean isGlobal = option.shopId() == null;
                boolean isOwned = option.shopId() != null && option.shopId().equals(shopId);
                if (!isGlobal && !isOwned) {
                    throw new PermissionDeniedException("Bạn không có quyền sử dụng Option: " + option.id());
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
                    String url = (info != null) ? info.url() : m.url();
                    boolean isThumb = isProductMedia && ((!hasThumbnail && m.sortOrder() == 0) || m.isThumbnail());
                    return ProductMediaSnapshot.of(m.mediaId(), url, m.alt(), isThumb, m.sortOrder());
                })
                .toList();
    }

    // ==========================================
    // 4. BUILDERS VÀ HELPERS
    // ==========================================

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

    private record ValidatedCreationContext(CategoryInfoDTO categoryInfo, Map<UUID, MediaInfoDTO> mediaMap) {
    }

    private record ValidatedUpdateContext(CategoryInfoDTO categoryInfo, Map<UUID, MediaInfoDTO> mediaMap) {
    }
}