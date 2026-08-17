package com.cuonglm.ecommerce.backend.location.entity;

import jakarta.persistence.*;

/**
 * <p>Ward entity</p>
 * Created By: CuongLM - 10/07/2025
 */
@Entity
@Table(name = "wards")
public class Ward {
    @Id
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    //<editor-fold desc="Getters/Setters">
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }
    //</editor-fold>
}

