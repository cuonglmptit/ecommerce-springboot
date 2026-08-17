package com.cuonglm.ecommerce.backend.location.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>District entity</p>
 * Created By: CuongLM - 10/07/2025
 */
@Entity
@Table(name = "districts")
public class District {
    @Id
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;

    @OneToMany(mappedBy = "district", cascade = CascadeType.ALL)
    private List<Ward> wards = new ArrayList<>();

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

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public List<Ward> getWards() {
        return wards;
    }

    public void setWards(List<Ward> wards) {
        this.wards = wards;
    }
    //</editor-fold>
}