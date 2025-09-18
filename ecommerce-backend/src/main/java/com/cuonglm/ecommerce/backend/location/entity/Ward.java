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

    private String name;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    //<editor-fold desc="Getters/Setters">

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public District getDistrict() {
        return district;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistrict(District district) {
        this.district = district;
    }
//</editor-fold>
}

