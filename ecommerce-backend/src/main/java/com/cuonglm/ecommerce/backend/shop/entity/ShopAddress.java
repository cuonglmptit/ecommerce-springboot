package com.cuonglm.ecommerce.backend.shop.entity;

import com.cuonglm.ecommerce.backend.location.entity.Location;
import jakarta.persistence.*;

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
@Table(name = "shop_addresses")
public class ShopAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false, unique = true)
    private Shop shop;

    @OneToOne(optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(length = 100)
    private String contactName;

    @Column(length = 15)
    private String phoneNumber;

    @Column(length = 255)
    private String note;
}
