package com.cuonglm.ecommerce.backend.user.entity;

import com.cuonglm.ecommerce.backend.location.entity.Location;
import com.cuonglm.ecommerce.backend.user.enums.UserAddressType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * UserAddress – Lớp lưu thông tin địa chỉ của user.
 *
 * <p>
 * Lưu thông tin các địa chỉ của User, địa chỉ mặc định,...
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 25 July 2025
 */
@Entity
@Table(name = "user_addresses")
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    @NotNull
    @Size(min = 10, max = 15)
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    private boolean isDefault;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserAddressType type;

    private String note;
}
