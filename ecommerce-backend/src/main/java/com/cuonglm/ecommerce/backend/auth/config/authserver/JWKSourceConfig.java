package com.cuonglm.ecommerce.backend.auth.config.authserver;

import com.cuonglm.ecommerce.backend.auth.config.keys.PemUtils;
import com.cuonglm.ecommerce.backend.auth.config.keys.RsaKeyProperties;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * JWKSourceConfig – Cấu hình JWKSource cho ứng dụng.
 *
 * <p>
 * Cấu hình hiện tại @Bean JWKSource sẽ bao gồm luôn @Bean JwtEncoder (cho auth server)
 * và @Bean JwtDecoder (cho Resource1 server), sau này tách ra thì sẽ cần cấu hình @Bean JwtDecoder cho Resource Server.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 22 October 2025
 */
@Configuration
@EnableConfigurationProperties(RsaKeyProperties.class)
public class JWKSourceConfig {

    private final RsaKeyProperties properties;
    private final PemUtils pemUtils;

    public JWKSourceConfig(RsaKeyProperties properties, PemUtils pemUtils) {
        this.properties = properties;
        this.pemUtils = pemUtils;
    }

    // Tạo Bean Private Key (Dễ dàng tái sử dụng/Autowired)
    @Bean
    public RSAPrivateKey rsaPrivateKey() throws Exception {
        return pemUtils.parsePrivateKey(properties.rsaPrivateKey());
    }

    // Tạo Bean Public Key (Dễ dàng tái sử dụng/Autowired)
    @Bean
    public RSAPublicKey rsaPublicKey() throws Exception {
        return pemUtils.parsePublicKey(properties.rsaPublicKey());
    }

    // Sử dụng các Bean Key đã được tạo để cấu hình JWKSource
    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {
        // Spring tự động inject 2 Bean Key trên vào đây
        RSAKey rsaKey = new RSAKey.Builder(rsaPublicKey)
                .privateKey(rsaPrivateKey).build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }
}