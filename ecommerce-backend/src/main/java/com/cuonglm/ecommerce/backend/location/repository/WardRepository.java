package com.cuonglm.ecommerce.backend.location.repository;

import com.cuonglm.ecommerce.backend.location.dto.internal.WardInfoView;
import com.cuonglm.ecommerce.backend.location.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * WardRepository – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Tuesday, 18 November 2025
 */
@Repository
public interface WardRepository extends JpaRepository<Ward, Integer> {
    List<WardInfoView> findByDistrictIdOrderByNameAsc(Integer districtId);

    /**
     * Tìm kiếm Ward theo ID, đồng thời kiểm tra Ward phải thuộc District và District phải thuộc Province tương ứng.
     * Sử dụng JOIN FETCH để tải District và Province trong cùng một truy vấn (giải quyết N+1).
     *
     * @param wardId     ID của Ward
     * @param districtId ID của District cha
     * @param provinceId ID của Province ông
     * @return Optional<Ward> Entity Ward đã được tải đầy đủ, hoặc rỗng nếu không khớp.
     */
    @Query("""
        SELECT w FROM Ward w
        JOIN FETCH w.district d
        JOIN FETCH d.province p
        WHERE w.id = :wardId
          AND d.id = :districtId
          AND p.id = :provinceId
    """)
    Optional<Ward> findByIdAndHierarchy(
            @Param("wardId") Integer wardId,
            @Param("districtId") Integer districtId,
            @Param("provinceId") Integer provinceId
    );
}
