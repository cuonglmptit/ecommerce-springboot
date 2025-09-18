package com.cuonglm.ecommerce.backend.shop.entity;

import com.cuonglm.ecommerce.backend.product.entity.Product;
import com.cuonglm.ecommerce.backend.user.entity.User;
import jakarta.persistence.*;

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
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 1000)
    private String description;

    // Địa chỉ shop (optional)
    @OneToOne(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private ShopAddress address;

    @OneToMany(mappedBy = "shop", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Product> products;

    //<editor-fold desc="Getters/Setters">

    public Long getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ShopAddress getAddress() {
        return address;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAddress(ShopAddress address) {
        this.address = address;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    //</editor-fold>
}
