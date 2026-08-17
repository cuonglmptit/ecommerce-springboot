package com.cuonglm.ecommerce.backend.location.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Province entity</p>
 * Created By: CuongLM - 10/07/2025
 */
@Entity
@Table(name = "provinces")
public class Province {
    @Id
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "province", cascade = CascadeType.ALL)
    private List<District> districts = new ArrayList<>();

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

    public List<District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }
    //</editor-fold>
}