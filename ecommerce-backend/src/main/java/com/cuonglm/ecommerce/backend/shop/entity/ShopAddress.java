package com.cuonglm.ecommerce.backend.shop.entity;

import com.cuonglm.ecommerce.backend.core.entity.AuditMetadata;
import com.cuonglm.ecommerce.backend.location.entity.snapshot.AddressSnapshot;
import com.cuonglm.ecommerce.backend.shop.enums.ShopAddressType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ShopAddress – Địa chỉ của shop.
 *
 * <p>
 * Địa chỉ của riêng của thực thể Shop.
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 25 July 2025
 */
@Entity
@Table(
        name = "shop_addresses",
        indexes = {
                @Index(name = "idx_shop_address_shop_id", columnList = "shop_id")
        }
)
@EntityListeners(AuditingEntityListener.class)
public class ShopAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShopAddressType type = ShopAddressType.PICKUP;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "address", columnDefinition = "jsonb", nullable = false)
    private AddressSnapshot address;

    @Column(length = 100)
    private String contactName;

    @Column(length = 15)
    private String phoneNumber;

    @Column(length = 255)
    private String note;

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

    public ShopAddressType getType() {
        return type;
    }

    public void setType(ShopAddressType type) {
        this.type = type;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public AddressSnapshot getAddress() {
        return address;
    }

    public void setAddress(AddressSnapshot address) {
        this.address = address;
    }

    public AuditMetadata getAudit() {
        return audit;
    }
    //</editor-fold>
}
