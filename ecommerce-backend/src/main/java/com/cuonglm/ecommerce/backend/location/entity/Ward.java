package com.cuonglm.ecommerce.backend.location.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "wards")
public class Ward {
    @Id
    private Integer id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;
}

