package com.cuonglm.ecommerce.backend.shop.repository;

import com.cuonglm.ecommerce.backend.shop.dto.internal.ShopInfoView;
import com.cuonglm.ecommerce.backend.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ShopRepository – Repository của {@link Shop}.
 *
 * @author cuonglmptit
 * @since Wednesday, 19 November 2025
 */
@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByOwnerId(Long ownerId);
    Optional<ShopInfoView> findShopInfoByOwnerId(Long ownerId);
    Optional<ShopInfoView> findShopInfoById(Long shopId);
    Optional<Shop> findFirstByOrderByIdAsc();
}
