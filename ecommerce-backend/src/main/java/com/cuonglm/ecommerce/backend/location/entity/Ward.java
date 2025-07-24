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
}

