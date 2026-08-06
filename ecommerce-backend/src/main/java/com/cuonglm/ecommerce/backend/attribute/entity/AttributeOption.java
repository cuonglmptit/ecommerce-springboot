package com.cuonglm.ecommerce.backend.attribute.entity;

import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;
import com.cuonglm.ecommerce.backend.shop.entity.Shop;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    @Column(nullable = false, name = "value")
    private String value; // Ví dụ: Đỏ, Đen, L, XL

    @Enumerated(EnumType.STRING)
    private AttributeScope scope = AttributeScope.GLOBAL;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AttributeStatus status = AttributeStatus.ACTIVE;

    //<editor-fold desc="Audit metadata">
    @CreatedBy
    @JoinColumn(name = "created_by_id", updatable = false)
    private Long createdById;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedBy
    @JoinColumn(name = "modified_by_id")
    private Long modifiedById;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant modifiedAt;
    //</editor-fold>

    //<editor-fold desc="Getters/Setters">
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop; // null nếu GLOBAL

    public static AttributeOption of(Attribute attribute, String value) {
        AttributeOption option = new AttributeOption();
        option.setAttribute(attribute);
        option.setValue(value);
        option.setScope(attribute.getScope());
        option.setShop(attribute.getShop());
        option.setStatus(AttributeStatus.ACTIVE);
        return option;
    }

    public UUID getId() {
        return id;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public String getValue() {
        return value;
    }

    public AttributeScope getScope() {
        return scope;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setScope(AttributeScope scope) {
        this.scope = scope;
    }

    public AttributeStatus getStatus() {
        return status;
    }

    public void setStatus(AttributeStatus status) {
        this.status = status;
    }

    //</editor-fold>
}
