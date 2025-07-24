package com.cuonglm.ecommerce.backend.location.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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

    private String name;

    @OneToMany(mappedBy = "province")
    private List<District> districts;
}
