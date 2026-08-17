package com.cuonglm.ecommerce.backend.location.repository;

import com.cuonglm.ecommerce.backend.location.dto.internal.ProvinceInfoView;
import com.cuonglm.ecommerce.backend.location.entity.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ProvinceRepository – Repository của {@link Province}.
 *
 * @author cuonglmptit
 * @since Tuesday, 18 November 2025
 */
@Repository
public interface ProvinceRepository extends JpaRepository<Province, Integer> {
    List<ProvinceInfoView> findAllByOrderByNameAsc();
}