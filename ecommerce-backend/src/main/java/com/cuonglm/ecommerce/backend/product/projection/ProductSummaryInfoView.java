package com.cuonglm.ecommerce.backend.product.projection;

import com.cuonglm.ecommerce.backend.product.entity.snapshot.ProductMediaSnapshot;
import com.cuonglm.ecommerce.backend.product.enums.ProductStatus;
import com.cuonglm.ecommerce.backend.product.enums.ProductVariantStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * ProductSummaryInfoView – Projection cho Product
 *
 * @author cuonglmptit
 * @since Saturday, 22 August 2026
 */
public interface ProductSummaryInfoView {
    Long getId();
    String getName();
    String getSlug();
    ProductStatus getStatus();
    ShopSummaryInfoView getShop();
    CategorySummaryInfoView getCategory();
    List<ProductMediaSnapshot> getMedia();
    List<VariantSummaryInfoView> getVariants();
    AuditSummaryInfoView getAudit();

    interface ShopSummaryInfoView {
        Long getId();
        String getName();
    }

    interface CategorySummaryInfoView {
        Long getId();
        String getName();
    }

    interface VariantSummaryInfoView {
        Long getId();
        BigDecimal getPrice();
        BigDecimal getSalePrice();
        Integer getStockQuantity();
        ProductVariantStatus getStatus();
    }

    interface AuditSummaryInfoView {
        Instant getCreatedAt();
    }
}