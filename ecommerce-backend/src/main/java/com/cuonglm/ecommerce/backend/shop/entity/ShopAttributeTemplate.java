package com.cuonglm.ecommerce.backend.shop.entity;

import com.cuonglm.ecommerce.backend.attribute.entity.Attribute;
import com.cuonglm.ecommerce.backend.core.entity.AuditMetadata;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ShopAttributeTemplate – Template attribute mà shop lưu lại để tái sử dụng.
 *
 * <p>
 * Entity này cho phép shop tạo "template" từ các global attributes để sử dụng
 * nhanh chóng khi tạo sản phẩm mới. Shop có thể tổ chức templates theo nhóm,
 * đánh dấu frequently used, và sắp xếp thứ tự hiển thị.
 * </p>
 *
 * @author cuonglmptit
 * @since Tuesday, 29 July 2025
 */
@Entity
@Table(name = "shop_attribute_templates",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"shop_id", "attribute_id"},
                name = "uk_shop_attribute_template")
)
public class ShopAttributeTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "shop_attribute_template_id")
    private List<ShopAttributeTemplateOption> attributeOptions = new ArrayList<>();

    @Embedded
    private AuditMetadata audit = new AuditMetadata();

    //<editor-fold desc="Getters/Setters">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public List<ShopAttributeTemplateOption> getAttributeOptions() {
        return attributeOptions;
    }

    public void setAttributeOptions(List<ShopAttributeTemplateOption> attributeOptions) {
        this.attributeOptions = attributeOptions;
    }

    public AuditMetadata getAudit() {
        return audit;
    }

    //</editor-fold>
}
