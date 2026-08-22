package com.cuonglm.ecommerce.backend.product.dto.external;

import com.cuonglm.ecommerce.backend.product.entity.Product;
import com.cuonglm.ecommerce.backend.product.entity.ProductVariant;
import com.cuonglm.ecommerce.backend.product.entity.snapshot.ProductMediaSnapshot;
import com.cuonglm.ecommerce.backend.product.entity.snapshot.SpecificationSnapshot;
import com.cuonglm.ecommerce.backend.product.entity.snapshot.VariantAttributeSnapshot;
import com.cuonglm.ecommerce.backend.product.enums.ProductVariantStatus;
import com.cuonglm.ecommerce.backend.product.projection.ProductDetailInfoView;

import java.math.BigDecimal;
import java.util.List;

/**
 * ProductResponse – DTO Response trả về thông tin chi tiết sản phẩm.
 *
 * @author cuonglmptit
 * @since Friday, 14 August 2026
 */
public record ProductResponse(
        Long id,
        Long shopId,
        Long categoryId,
        String name,
        String description,
        String status,
        List<SpecificationSnapshot> specifications,
        List<ProductMediaSnapshot> media,
        List<VariantItem> variants
) {
    public record VariantItem(
            Long id,
            String sku,
            BigDecimal price,
            BigDecimal salePrice,
            Integer stockQuantity,
            ProductVariantStatus status,
            List<VariantAttributeSnapshot> attributes,
            List<ProductMediaSnapshot> media
    ) {
        public static VariantItem fromEntity(ProductVariant variant) {
            if (variant == null) return null;
            return new VariantItem(
                    variant.getId(),
                    variant.getSku(),
                    variant.getPrice(),
                    variant.getSalePrice(),
                    variant.getStockQuantity(),
                    variant.getStatus(),
                    variant.getAttributes() != null ? variant.getAttributes() : List.of(),
                    variant.getMedia() != null ? variant.getMedia() : List.of()
            );
        }

        public static VariantItem fromView(ProductDetailInfoView.VariantDetailInfoView view) {
            if (view == null) return null;
            return new VariantItem(
                    view.getId(),
                    view.getSku(),
                    view.getPrice(),
                    view.getSalePrice(),
                    view.getStockQuantity(),
                    view.getStatus(),
                    view.getAttributes() != null ? view.getAttributes() : List.of(),
                    view.getMedia() != null ? view.getMedia() : List.of()
            );
        }
    }

    public static ProductResponse fromEntity(Product product) {
        if (product == null) return null;

        List<VariantItem> variantItems = (product.getVariants() != null) ?
                product.getVariants().stream().map(VariantItem::fromEntity).toList() : List.of();

        return new ProductResponse(
                product.getId(),
                product.getShop() != null ? product.getShop().getId() : null,
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getName(),
                product.getDescription(),
                product.getStatus() != null ? product.getStatus().name() : null,
                product.getSpecifications() != null ? product.getSpecifications() : List.of(),
                product.getMedia() != null ? product.getMedia() : List.of(),
                variantItems
        );
    }

    public static ProductResponse fromView(ProductDetailInfoView view) {
        if (view == null) return null;
        List<VariantItem> variantItems = (view.getVariants() != null) ?
                view.getVariants().stream().map(VariantItem::fromView).toList() : List.of();

        return new ProductResponse(
                view.getId(),
                view.getShop() != null ? view.getShop().getId() : null,
                view.getCategory() != null ? view.getCategory().getId() : null,
                view.getName(),
                view.getDescription(),
                view.getStatus() != null ? view.getStatus().name() : null,
                view.getSpecifications() != null ? view.getSpecifications() : List.of(),
                view.getMedia() != null ? view.getMedia() : List.of(),
                variantItems
        );
    }
}