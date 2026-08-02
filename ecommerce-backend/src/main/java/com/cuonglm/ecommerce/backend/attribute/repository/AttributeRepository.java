package com.cuonglm.ecommerce.backend.attribute.repository;

import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeInfoView;
import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeOptionInfoDTO;
import com.cuonglm.ecommerce.backend.attribute.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * AttributeRepository – Repository cho {@link Attribute}
 *
 * @author cuonglmptit
 * @since Saturday, 29 November 2025
 */
@Repository
public interface AttributeRepository extends JpaRepository<Attribute, UUID> {
    /**
     * Tìm thông tin của một {@link Attribute} thông qua projection bởi {@link AttributeInfoView}
     *
     * @return Một proxy {@link AttributeInfoView} chứa thông tin hoặc {@link Optional#empty()}
     */
    Optional<AttributeInfoView> findAttributeInfoById(UUID uuid);

    /**
     * Kiểm tra Attribute có tồn tại theo Shop ID và Code không
     *
     * @param shopId Id của Shop
     * @param code Code của Attribute
     * @return
     */
    boolean existsByShopIdAndCode(Long shopId, String code);

    Optional<Attribute> findByCode(String code);

    Optional<Attribute> findByShopIdAndCode(Long shopId, String code);
}
