package com.cuonglm.ecommerce.backend.shop.entity;

import com.cuonglm.ecommerce.backend.location.entity.snapshot.AddressSnapshot;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "address", columnDefinition = "jsonb", nullable = false)
    private AddressSnapshot address;

    @Column(length = 100)
    private String contactName;

    @Column(length = 15)
    private String phoneNumber;

    @Column(length = 255)
    private String note;
}
