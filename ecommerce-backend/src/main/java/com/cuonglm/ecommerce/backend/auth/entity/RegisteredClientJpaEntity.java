package com.cuonglm.ecommerce.backend.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * RegisteredClientJpa – Thực thể JPA của RegisteredClient.
 *
 * <p>
 * Mapping dữ liệu RegisteredClient của Spring Authorization Server vào Database.
 * </p>
 *
 * @author cuonglmptit
 * @since Sunday, 19 October 2025
 */
@Entity
@Table(
        name = "registered_clients"
)
public class RegisteredClientJpaEntity {
    @Id
    private String id;

    @NotNull
    @Column(name = "client_id", unique = true, nullable = false)
    private String clientId;

    @NotNull
    @Column(nullable = false)
    private Instant clientIdIssuedAt;

    private Instant clientSecretExpiresAt;

    private String clientSecret;
    private String clientName;

    @NotNull
    @Column(name = "authentication_methods", nullable = false)
    private String clientAuthenticationMethods;

    @NotNull
    @Column(name = "authorization_grant_types", nullable = false)
    private String authorizationGrantTypes;

    private String redirectUris;

    @NotNull
    @Column(nullable = false)
    private String scopes;

    @Column(columnDefinition = "TEXT")
    private String clientSettings;
    @Column(columnDefinition = "TEXT")
    private String tokenSettings;

    public RegisteredClientJpaEntity() {
    }

    //<editor-fold desc="Getters">
    public String getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public Instant getClientIdIssuedAt() {
        return clientIdIssuedAt;
    }

    public Instant getClientSecretExpiresAt() {
        return clientSecretExpiresAt;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientAuthenticationMethods() {
        return clientAuthenticationMethods;
    }

    public String getAuthorizationGrantTypes() {
        return authorizationGrantTypes;
    }

    public String getRedirectUris() {
        return redirectUris;
    }

    public String getScopes() {
        return scopes;
    }

    public String getClientSettings() {
        return clientSettings;
    }

    public String getTokenSettings() {
        return tokenSettings;
    }

    //</editor-fold>
    //<editor-fold desc="Setters">
    public void setId(String id) {
        this.id = id;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientIdIssuedAt(Instant clientIdIssuedAt) {
        this.clientIdIssuedAt = clientIdIssuedAt;
    }

    public void setClientSecretExpiresAt(Instant clientSecretExpiresAt) {
        this.clientSecretExpiresAt = clientSecretExpiresAt;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setClientAuthenticationMethods(String clientAuthenticationMethods) {
        this.clientAuthenticationMethods = clientAuthenticationMethods;
    }

    public void setAuthorizationGrantTypes(String authorizationGrantTypes) {
        this.authorizationGrantTypes = authorizationGrantTypes;
    }

    public void setRedirectUris(String redirectUris) {
        this.redirectUris = redirectUris;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public void setClientSettings(String clientSettings) {
        this.clientSettings = clientSettings;
    }

    public void setTokenSettings(String tokenSettings) {
        this.tokenSettings = tokenSettings;
    }

    //</editor-fold>
}
