package com.cuonglm.ecommerce.backend.user.repository;

import com.cuonglm.ecommerce.backend.user.entity.UserOAuth2Account;
import com.cuonglm.ecommerce.backend.user.enums.OAuth2Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * UserOAuth2AccountRepository – Repo quản lý {@link UserOAuth2Account}.
 *
 * <p>
 * Chứa các method để thao tác với {@link UserOAuth2Account}.
 * </p>
 *
 * @author cuonglmptit
 * @since Thursday, 30 October 2025
 */
public interface UserOAuth2AccountRepository extends JpaRepository<UserOAuth2Account, Long> {
    Optional<UserOAuth2Account> findByProviderAndProviderUserId(OAuth2Provider provider, String providerUserId);
}
