package com.cuonglm.ecommerce.backend.attribute.entity;

import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;
import com.cuonglm.ecommerce.backend.core.entity.AuditMetadata;
import com.cuonglm.ecommerce.backend.shop.entity.Shop;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

/**
 * AttributeOption – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Saturday, 26 July 2025
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "attribute_options", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"attribute_id", "value", "shop_id"})
})
public class AttributeOption {
    @PrePersist
    public void ensureId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    @Column(name = "value", nullable = false, length = 255)
    private String value; // Ví dụ: Đỏ, Đen, L, XL

    @Enumerated(EnumType.STRING)
    private AttributeScope scope = AttributeScope.GLOBAL;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AttributeStatus status = AttributeStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop; // null nếu GLOBAL

    @Embedded
    private AuditMetadata audit = new AuditMetadata();

    public static AttributeOption of(Attribute attribute, String value) {
        AttributeOption option = new AttributeOption();
        option.setAttribute(attribute);
        option.setValue(value);
        option.setScope(attribute.getScope());
        option.setShop(attribute.getShop());
        option.setStatus(AttributeStatus.ACTIVE);
        return option;
    }

    //<editor-fold desc="Getters/Setters">

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public AttributeScope getScope() {
        return scope;
    }

    public void setScope(AttributeScope scope) {
        this.scope = scope;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public AttributeStatus getStatus() {
        return status;
    }

    public void setStatus(AttributeStatus status) {
        this.status = status;
    }

    public AuditMetadata getAudit() {
        return audit;
    }
    //</editor-fold>
}
