package com.cuonglm.ecommerce.backend.product.projection;

import com.cuonglm.ecommerce.backend.product.entity.snapshot.ProductMediaSnapshot;
import com.cuonglm.ecommerce.backend.product.entity.snapshot.SpecificationSnapshot;
import com.cuonglm.ecommerce.backend.product.entity.snapshot.VariantAttributeSnapshot;
import com.cuonglm.ecommerce.backend.product.enums.ProductStatus;
import com.cuonglm.ecommerce.backend.product.enums.ProductVariantStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * ProductDetailInfoView – Interface Closed Projection cho chi tiết sản phẩm.
 *
 * @author cuonglmptit
 * @since Saturday, 22 August 2026
 */
public interface ProductDetailInfoView {
    Long getId();
    String getName();
    String getSlug();
    String getDescription();
    ProductStatus getStatus();
    List<SpecificationSnapshot> getSpecifications();
    List<ProductMediaSnapshot> getMedia();
    ShopDetailInfoView getShop();
    CategoryDetailInfoView getCategory();
    List<VariantDetailInfoView> getVariants();
    AuditDetailInfoView getAudit();

    interface ShopDetailInfoView {
        Long getId();
        String getName();
    }

    interface CategoryDetailInfoView {
        Long getId();
        String getName();
    }

    interface VariantDetailInfoView {
        Long getId();
        String getSku();
        BigDecimal getPrice();
        BigDecimal getSalePrice();
        Integer getStockQuantity();
        ProductVariantStatus getStatus();
        List<VariantAttributeSnapshot> getAttributes();
        List<ProductMediaSnapshot> getMedia();
    }

    interface AuditDetailInfoView {
        Instant getCreatedAt();
        Instant getModifiedAt();
    }
}