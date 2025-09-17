package com.cuonglm.ecommerce.backend.auth.config.resourceserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * ResourceServerConfig – Lớp cấu hình cho phần Resource Server (sau này).
 *
 * <p>
 * Cấu hình bảo vệ các api tài nguyên ứng dụng, để @Order(2) cho các api endpoint, @Order(3) cho endpoint login.
 * </p>
 *
 * @author cuonglmptit
 * @since Tuesday, 21 October 2025
 */
@Configuration
public class ResourceServerConfig {
    // SecurityFilterChain cho Resource Server, quản lý các endpoint /api/**
    private final JwtAuthenticationConverter customJwtAuthenticationConverter;
    private final JwtDecoder jwtDecoder;

    public ResourceServerConfig(JwtAuthenticationConverter customJwtAuthenticationConverter, JwtDecoder jwtDecoder) {
        this.customJwtAuthenticationConverter = customJwtAuthenticationConverter;
        this.jwtDecoder = jwtDecoder;
    }

    @Bean
    @Order(3)
    public SecurityFilterChain jwtFilterChain(HttpSecurity http)
            throws Exception {
        http.securityMatcher("/api/**")
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .requestMatchers(HttpMethod.GET, "/api/v1/users/all").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                // Ignoring the session cookie
                .sessionManagement(configurer ->
                        configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(customJwtAuthenticationConverter)
                                // Sử dụng JwtDecoder đã được cấu hình trong JwtDecoderConfig
                                .decoder(jwtDecoder)
                        )
                );
        return http.build();
    }
}
