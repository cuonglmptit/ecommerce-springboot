package com.cuonglm.ecommerce.backend.user.entity;

import com.cuonglm.ecommerce.backend.user.enums.OAuth2Provider;
import jakarta.persistence.*;

/**
 * UserOAuth2Account – Thực thể đăng nhập theo oauth.
 *
 * <p>
 * Lớp liên kết @ManyToOne với User để thực hiện đăng nhập bằng bên thứ 3.
 * </p>
 *
 * @author cuonglmptit
 * @since Sunday, 20 July 2025
 */
@Entity
@Table(
        name = "user_oauth2_account",
        uniqueConstraints = {
                // Mỗi cặp (provider, providerUserId) là duy nhất và chỉ được liên kết với một User
                @UniqueConstraint(
                        columnNames = {"provider", "provider_user_id"}
                )
        }
)
public class UserOAuth2Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OAuth2Provider provider;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //<editor-fold desc="Getters">
    public Long getId() {
        return id;
    }

    public OAuth2Provider getProvider() {
        return provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public User getUser() {
        return user;
    }
    //</editor-fold>

    //<editor-fold desc="Setters">
    public void setId(Long id) {
        this.id = id;
    }

    public void setProvider(OAuth2Provider provider) {
        this.provider = provider;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public void setUser(User user) {
        this.user = user;
    }
    //</editor-fold>
}
