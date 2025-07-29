package com.cuonglm.ecommerce.backend.shop.entity;

import com.cuonglm.ecommerce.backend.attribute.entity.Attribute;
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

    // Metadata: shop muốn set sẵn option để dùng nhanh
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "shop_attribute_template_id")
    private List<ShopAttributeTemplateOption> attributeOptions = new ArrayList<>();
}
