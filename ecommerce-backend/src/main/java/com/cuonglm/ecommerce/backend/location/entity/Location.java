package com.cuonglm.ecommerce.backend.location.entity;

/**
 * Location – Đại diện cho 1 địa điểm đầy đủ gồm Tỉnh, Huyện, Xã, địa chỉ cụ thể.
 *
 * <p>
 * Đây là thực thể dùng chung cho các địa chỉ của User, Shop, v.v.
 * Dùng để snapshot vị trí khi tạo, không tái sử dụng.
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 25 July 2025
 */

import com.cuonglm.ecommerce.backend.user.entity.User;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ward_id", nullable = false)
    private Ward ward;

    @Column(nullable = false, length = 255)
    private String addressLine;

    // === Mở rộng địa lý để sau này có thể dùng Google Maps APIs ===
    //Phần này cũng chưa thực sự cần vì hệ thống hiện tại nhỏ, muốn scale dễ nên để sẵn
    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(length = 255)
    private String placeId;

    @Column(length = 255)
    private String formattedAddress;

    // Metadata
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

    //<editor-fold desc="Getters/Setters">

    public Long getId() {
        return id;
    }

    public Province getProvince() {
        return province;
    }

    public District getDistrict() {
        return district;
    }

    public Ward getWard() {
        return ward;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public User getModifiedBy() {
        return modifiedBy;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public void setWard(Ward ward) {
        this.ward = ward;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setModifiedBy(User modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    //</editor-fold>
}
