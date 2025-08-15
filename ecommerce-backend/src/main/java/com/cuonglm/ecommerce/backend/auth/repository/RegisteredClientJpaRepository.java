package com.cuonglm.ecommerce.backend.auth.repository;

import com.cuonglm.ecommerce.backend.auth.entity.RegisteredClientJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * RegisteredClientJpaRepository – Interface cho việc truy vấn {@link RegisteredClientJpaEntity}.
 *
 * <p>
 * Hỗ trợ các phương thức CRUD {@link RegisteredClientJpaEntity}.
 * </p>
 *
 * @author cuonglmptit
 * @since Sunday, 19 October 2025
 */
@Repository
public interface RegisteredClientJpaRepository extends JpaRepository<RegisteredClientJpaEntity, String> {
    Optional<RegisteredClientJpaEntity> findByClientId(String clientId);
}
