package com.cuonglm.ecommerce.backend.attribute.repository;

import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeOptionInfoView;
import com.cuonglm.ecommerce.backend.attribute.entity.Attribute;
import com.cuonglm.ecommerce.backend.attribute.entity.AttributeOption;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * AttributeOptionRepository – Repository cho {@link AttributeOption}.
 *
 * @author cuonglmptit
 * @since Saturday, 29 November 2025
 */
public interface AttributeOptionRepository extends JpaRepository<AttributeOption, UUID> {
    List<AttributeOptionInfoView> findAllByIdIn(List<UUID> ids);

    /**
     * Tìm thông tin của một {@link AttributeOption} thông qua projection bởi {@link AttributeOptionInfoView}
     *
     * @return Một proxy {@link AttributeOptionInfoView} chứa thông tin hoặc {@link Optional#empty()}
     */
    Optional<AttributeOptionInfoView> findAttributeOptionInfoById(UUID uuid);

    Collection<Object> findByAttributeAndValue(Attribute attribute, String value);

    Optional<AttributeOption> findByAttributeAndValueIgnoreCase(Attribute attribute, String value);

    /**
     * Tìm kiếm Option theo tên Attribute cha, từ khóa giá trị, loại Type và Status.
     *
     */
    @Query("""
        SELECT ao FROM AttributeOption ao
        JOIN ao.attribute a
        WHERE (:shopId IS NULL OR ao.shop.id = :shopId OR ao.shop IS NULL)
          AND (:attributeName IS NULL OR :attributeName = '' OR LOWER(a.name) = LOWER(:attributeName))
          AND (:types IS NULL OR a.type IN :types)
          AND (:statuses IS NULL OR ao.status IN :statuses)
          AND (:value IS NULL OR :value = '' OR LOWER(ao.value) LIKE LOWER(CONCAT('%', :value, '%')))
        ORDER BY ao.value ASC
    """)
    List<AttributeOptionInfoView> searchAttributeOptions(
            @Param("shopId") Long shopId,
            @Param("attributeName") String attributeName,
            @Param("types") List<AttributeType> types,
            @Param("statuses") List<AttributeStatus> statuses,
            @Param("value") String value
    );
}
