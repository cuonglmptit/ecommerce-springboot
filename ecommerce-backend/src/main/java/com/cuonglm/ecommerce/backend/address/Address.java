package com.cuonglm.ecommerce.backend.address;

import com.cuonglm.ecommerce.backend.location.entity.District;
import com.cuonglm.ecommerce.backend.location.entity.Province;
import com.cuonglm.ecommerce.backend.location.entity.Ward;
import com.cuonglm.ecommerce.backend.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

/**
 * <p>Address entity</p>
 * Created By: CuongLM - 10/07/2025
 */
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    @NotNull
    @Size(min = 10, max = 15)
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    private Ward ward;

    private String addressLine;

    @Column(nullable = false)
    private boolean isDefault;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", updatable = false)
    private User createdBy;
    @CreatedDate
    private Instant createdAt;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modified_by_id")
    private User modifiedBy;
    @LastModifiedDate
    private Instant modifiedAt;
}
