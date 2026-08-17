package com.cuonglm.ecommerce.backend.location.repository;

import com.cuonglm.ecommerce.backend.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * LocationRepository – Repository cho {@link Location}.
 *
 * @author cuonglmptit
 * @since Friday, 25 July 2025
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("""
        SELECT l FROM Location l
        JOIN FETCH l.province
        JOIN FETCH l.district
        JOIN FETCH l.ward
        WHERE l.id = :id
    """)
    Optional<Location> findLocationWithHierarchyById(@Param("id") Long id);
}