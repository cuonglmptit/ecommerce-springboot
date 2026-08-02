package com.cuonglm.ecommerce.backend.attribute.repository;

import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeOptionInfoView;
import com.cuonglm.ecommerce.backend.attribute.entity.Attribute;
import com.cuonglm.ecommerce.backend.attribute.entity.AttributeOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * AttributeOptionRepository – Repository cho {@link AttributeOption}
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
}
