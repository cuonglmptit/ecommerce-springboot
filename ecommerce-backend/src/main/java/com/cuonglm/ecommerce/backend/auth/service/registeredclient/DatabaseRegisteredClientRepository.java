package com.cuonglm.ecommerce.backend.auth.service.registeredclient;

import com.cuonglm.ecommerce.backend.auth.entity.RegisteredClientJpaEntity;
import com.cuonglm.ecommerce.backend.auth.repository.RegisteredClientJpaRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DatabaseRegisteredClientRepository – Adapter chuyển đổi giữa RegisteredClientJpaEntity và  RegisteredClient.
 *
 * <p>
 * Chuyển đổi giữa RegisteredClientJpaEntity(của hệ thống) và RegisteredClient (của Spring Auth Server).
 * </p>
 *
 * @author cuonglmptit
 * @since Sunday, 19 October 2025
 */
@Component
public class DatabaseRegisteredClientRepository implements RegisteredClientRepository {

    private final RegisteredClientJpaRepository registeredClientJpaRepository;

    public DatabaseRegisteredClientRepository(RegisteredClientJpaRepository registeredClientJpaRepository) {
        this.registeredClientJpaRepository = registeredClientJpaRepository;
    }

    @Override
    @Transactional
    public void save(RegisteredClient registeredClient) {
        RegisteredClientJpaEntity entity = mapToEntity(registeredClient);
        registeredClientJpaRepository.save(entity);
    }

    @Override
    public RegisteredClient findById(String id) {
        return registeredClientJpaRepository.findById(id)
                .map(this::mapToRegisteredClient)
                .orElse(null);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return registeredClientJpaRepository.findByClientId(clientId)
                .map(this::mapToRegisteredClient)
                .orElse(null);
    }

    private RegisteredClientJpaEntity mapToEntity(RegisteredClient client) {
        RegisteredClientJpaEntity entity;
        if (client.getId() != null) {
            // Kiểm tra DB
            entity = registeredClientJpaRepository.findById(client.getId())
                    .orElse(new RegisteredClientJpaEntity());
        } else {
            entity = new RegisteredClientJpaEntity();
        }
        entity.setId(client.getId() != null ? client.getId() : UUID.randomUUID().toString());
        entity.setClientId(client.getClientId());
        entity.setClientIdIssuedAt(client.getClientIdIssuedAt());
        entity.setClientSecret(client.getClientSecret());
        entity.setClientSecretExpiresAt(client.getClientSecretExpiresAt());
        entity.setClientName(client.getClientName());
        entity.setClientAuthenticationMethods(join(client.getClientAuthenticationMethods()
                .stream().map(ClientAuthenticationMethod::getValue).collect(Collectors.toSet())));
        entity.setAuthorizationGrantTypes(join(client.getAuthorizationGrantTypes()
                .stream().map(AuthorizationGrantType::getValue).collect(Collectors.toSet())));
        entity.setRedirectUris(join(client.getRedirectUris()));
        entity.setScopes(join(client.getScopes()));
        entity.setClientIdIssuedAt(client.getClientIdIssuedAt());

        entity.setClientSettings(client.getClientSettings().toString());
        entity.setTokenSettings(client.getTokenSettings().toString());
        return entity;
    }

    private RegisteredClient mapToRegisteredClient(RegisteredClientJpaEntity entity) {
        RegisteredClient.Builder builder = RegisteredClient.withId(entity.getId().toString())
                .clientId(entity.getClientId())
                .clientIdIssuedAt(entity.getClientIdIssuedAt())
                .clientSecret(entity.getClientSecret())
                .clientSecretExpiresAt(entity.getClientSecretExpiresAt())
                .clientName(entity.getClientName());

        split(entity.getClientAuthenticationMethods()).forEach(v -> builder.clientAuthenticationMethod(new ClientAuthenticationMethod(v)));
        split(entity.getAuthorizationGrantTypes()).forEach(v -> builder.authorizationGrantType(new AuthorizationGrantType(v)));
        split(entity.getRedirectUris()).forEach(builder::redirectUri);
        split(entity.getScopes()).forEach(builder::scope);

        builder.clientSettings(ClientSettings.builder().build());
        builder.tokenSettings(TokenSettings.builder().build());
        return builder.build();
    }

    // Phương thức giúp join Set thành 1 string để lưu vào Database
    private String join(Set<String> set) {
        return String.join(",", new ArrayList<>(set));
    }

    // Phương thức giúp tách chuỗi ra từ String theo dấu "," để chuyển đổi lại thành Set
    private Set<String> split(String str) {
        return (str == null || str.isBlank())
                ? Set.of()
                : Arrays.stream(str.split(",")).map(String::trim).collect(Collectors.toSet());
    }
}