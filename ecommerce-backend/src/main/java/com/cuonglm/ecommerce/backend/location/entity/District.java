package com.cuonglm.ecommerce.backend.location.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "districts")
public class District {
    @Id
    private Integer id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;

    @OneToMany(mappedBy = "district")
    private List<Ward> wards;
}

