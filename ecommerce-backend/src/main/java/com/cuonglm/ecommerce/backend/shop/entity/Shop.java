package com.cuonglm.ecommerce.backend.shop.entity;

import com.cuonglm.ecommerce.backend.core.entity.AuditMetadata;
import com.cuonglm.ecommerce.backend.product.entity.Product;
import com.cuonglm.ecommerce.backend.shop.enums.ShopStatus;
import com.cuonglm.ecommerce.backend.user.entity.User;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Shop – Thực thể Shop.
 *
 * <p>
 * Shop bao gồm các thông tin về cửa hàng, liên kết với các sản phẩm của cửa hàng,....
 * </p>
 *
 * @author cuonglmptit
 * @since Sunday, 20 July 2025
 */
@Entity
@Table(name = "shops")
@EntityListeners(AuditingEntityListener.class)
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    private User owner;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 1000)
    private String description;

    // Địa chỉ shop (optional)
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ShopAddress> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "shop", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Product> products;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShopStatus status = ShopStatus.ACTIVE;

    @Embedded
    private AuditMetadata audit = new AuditMetadata();


    //<editor-fold desc="Getters/Setters">

    public Long getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public ShopStatus getStatus() {
        return status;
    }

    public void setStatus(ShopStatus status) {
        this.status = status;
    }

    public List<ShopAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<ShopAddress> addresses) {
        this.addresses = addresses;
    }

    public AuditMetadata getAudit() {
        return audit;
    }
    //</editor-fold>
}
