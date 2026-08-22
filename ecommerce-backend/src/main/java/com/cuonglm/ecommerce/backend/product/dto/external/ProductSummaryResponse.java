package com.cuonglm.ecommerce.backend.product.dto.external;

import com.cuonglm.ecommerce.backend.product.entity.Product;
import com.cuonglm.ecommerce.backend.product.entity.ProductVariant;
import com.cuonglm.ecommerce.backend.product.entity.snapshot.ProductMediaSnapshot;
import com.cuonglm.ecommerce.backend.product.enums.ProductStatus;
import com.cuonglm.ecommerce.backend.product.projection.ProductSummaryInfoView;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * ProductSummaryResponse – DTO response cho các trang danh sách, tìm kiếm, giỏ hàng gợi ý.
 *
 * @author cuonglmptit
 * @since Saturday, 22 August 2026
 */
public record ProductSummaryResponse(
        Long id,
        String name,
        String slug,
        ProductStatus status,
        Long shopId,
        String shopName,
        Long categoryId,
        String categoryName,
        String thumbnailUrl,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Integer totalStock,
        Instant createdAt
) {
    public static ProductSummaryResponse fromEntity(Product product) {
        if (product == null) return null;

        String thumbUrl = product.getMedia().stream()
                .filter(ProductMediaSnapshot::isThumbnail)
                .map(ProductMediaSnapshot::url)
                .findFirst()
                .orElseGet(() -> product.getMedia().isEmpty() ? null : product.getMedia().get(0).url());

        List<ProductVariant> variants = product.getVariants() != null ? product.getVariants() : List.of();

        BigDecimal min = variants.stream()
                .map(v -> v.getSalePrice() != null ? v.getSalePrice() : v.getPrice())
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        BigDecimal max = variants.stream()
                .map(ProductVariant::getPrice)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        int totalStock = variants.stream()
                .mapToInt(v -> v.getStockQuantity() != null ? v.getStockQuantity() : 0)
                .sum();

        return new ProductSummaryResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getStatus(),
                product.getShop() != null ? product.getShop().getId() : null,
                product.getShop() != null ? product.getShop().getName() : null,
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null,
                thumbUrl,
                min,
                max,
                totalStock,
                product.getAudit() != null ? product.getAudit().getCreatedAt() : null
        );
    }

    public static ProductSummaryResponse fromView(ProductSummaryInfoView view) {
        if (view == null) return null;

        String thumbUrl = null;
        if (view.getMedia() != null && !view.getMedia().isEmpty()) {
            thumbUrl = view.getMedia().stream()
                    .filter(ProductMediaSnapshot::isThumbnail)
                    .map(ProductMediaSnapshot::url)
                    .findFirst()
                    .orElseGet(() -> view.getMedia().get(0).url());
        }

        List<ProductSummaryInfoView.VariantSummaryInfoView> variants =
                view.getVariants() != null ? view.getVariants() : List.of();

        BigDecimal min = variants.stream()
                .map(v -> v.getSalePrice() != null ? v.getSalePrice() : v.getPrice())
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        BigDecimal max = variants.stream()
                .map(ProductSummaryInfoView.VariantSummaryInfoView::getPrice)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        int totalStock = variants.stream()
                .mapToInt(v -> v.getStockQuantity() != null ? v.getStockQuantity() : 0)
                .sum();

        return new ProductSummaryResponse(
                view.getId(),
                view.getName(),
                view.getSlug(),
                view.getStatus(),
                view.getShop() != null ? view.getShop().getId() : null,
                view.getShop() != null ? view.getShop().getName() : null,
                view.getCategory() != null ? view.getCategory().getId() : null,
                view.getCategory() != null ? view.getCategory().getName() : null,
                thumbUrl,
                min,
                max,
                totalStock,
                view.getAudit() != null ? view.getAudit().getCreatedAt() : null
        );
    }
}