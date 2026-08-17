package com.cuonglm.ecommerce.backend.location.repository;

import com.cuonglm.ecommerce.backend.location.dto.internal.DistrictInfoView;
import com.cuonglm.ecommerce.backend.location.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DistrictRepository – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Tuesday, 18 November 2025
 */
@Repository
public interface DistrictRepository extends JpaRepository<District, Integer> {
    List<DistrictInfoView> findByProvinceIdOrderByNameAsc(Integer provinceId);
}