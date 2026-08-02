package com.cuonglm.ecommerce.backend.product.entity;

import com.cuonglm.ecommerce.backend.attribute.entity.Attribute;
import com.cuonglm.ecommerce.backend.attribute.entity.AttributeOption;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * ProductSpecification – Lưu thông tin chi tiết về sản phẩm.
 *
 * <p>
 * Lưu thông tin như Hãng sản xuất, Chất liệu,...
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 06 February 2026
 */
@Entity
@Table(
        name = "product_specifications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "attribute_id"})
)
public class ProductSpecification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    /**
     * Trường hợp chọn từ danh sách có sẵn (Dropdown)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "attribute_option_id")
    private AttributeOption attributeOption;

    /**
     * Trường hợp nhập tay (Free text)
     * VD: Model = "IP15-TITAN"
     */
    @Column(name = "raw_value")
    private String rawValue;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public AttributeOption getAttributeOption() {
        return attributeOption;
    }

    public void setAttributeOption(AttributeOption attributeOption) {
        this.attributeOption = attributeOption;
    }

    public String getRawValue() {
        return rawValue;
    }

    public void setRawValue(String rawValue) {
        this.rawValue = rawValue;
    }
}
