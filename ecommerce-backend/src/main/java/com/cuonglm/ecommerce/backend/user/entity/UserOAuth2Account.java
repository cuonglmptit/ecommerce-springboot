package com.cuonglm.ecommerce.backend.user.entity;

import com.cuonglm.ecommerce.backend.core.entity.AuditMetadata;
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
                        name = "uk_oauth2_provider_user_id",
                        columnNames = {"provider", "provider_user_id"}
                )
        }
)
public class UserOAuth2Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OAuth2Provider provider;

    @Column(name = "provider_user_id", nullable = false, length = 128)
    private String providerUserId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Embedded
    private AuditMetadata audit = new AuditMetadata();

    //<editor-fold desc="Getters/Setters">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OAuth2Provider getProvider() {
        return provider;
    }

    public void setProvider(OAuth2Provider provider) {
        this.provider = provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AuditMetadata getAudit() {
        return audit;
    }
    //</editor-fold>
}
