package com.cuonglm.ecommerce.backend.attribute.entity;

import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeType;
import com.cuonglm.ecommerce.backend.shop.entity.Shop;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Attribute – Mô_tả_ngắn_về_lớp.
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
@Table(name = "attributes",
        uniqueConstraints = {
                //Nếu GLOBAL không có shop_id:
                @UniqueConstraint(columnNames = "code"),
                // Ràng buộc cho Attribute SHOP (Shop ID + Code phải là duy nhất)
                @UniqueConstraint(columnNames = {"shop_id", "code"})
        }
)

public class Attribute {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name; // Ví dụ: Màu sắc, Kích thước

    // "COLOR", "SIZE" - for technical use
    @Column(nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    private AttributeScope scope = AttributeScope.GLOBAL;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttributeOption> attributeOptions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop; // null nếu GLOBAL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttributeType type = AttributeType.SPECIFICATION;

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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public AttributeScope getScope() {
        return scope;
    }

    public void setScope(AttributeScope scope) {
        this.scope = scope;
    }

    public AttributeType getType() {
        return type;
    }

    public void setType(AttributeType type) {
        this.type = type;
    }

    public List<AttributeOption> getAttributeOptions() {
        return attributeOptions;
    }

    public void setAttributeOptions(List<AttributeOption> attributeOptions) {
        this.attributeOptions = attributeOptions;
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

    //</editor-fold>
}
