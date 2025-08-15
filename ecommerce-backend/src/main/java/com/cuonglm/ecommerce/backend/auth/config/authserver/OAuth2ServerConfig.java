package com.cuonglm.ecommerce.backend.auth.config.authserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * OAuth2ServerConfig – Cấu hình cho các endpoint của Authorization Server.
 *
 * <p>
 * Hiên tại SecurityFilterChain là Order(1) để có thể tách luồng ra đăng nhập lấy token từ các endpoint của AS.
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 08 August 2025
 */
@Configuration
public class OAuth2ServerConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        /* //Cách cũ deprecated
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class).oidc(Customizer.withDefaults());
        */

        // Dùng DSL mới thay cho applyDefaultSecurity
        // Lấy ra 1 instance của OAuth2AuthorizationServerConfigurer và cấu hình OIDC luôn
        // method .authorizationServer() tương đương new OAuth2AuthorizationServerConfigurer()
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer().oidc(Customizer.withDefaults());
        http
                // Áp dụng cấu hình cho Authorization Server (bao gồm OAuth2 + OIDC)
                .with(authorizationServerConfigurer, Customizer.withDefaults())
                // Chỉ áp dụng Security FilterChain cho các endpoint của Authorization Server
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                // Tất cả các request đến các endpoint của AS đều cần xác thực
                // (giúp tạo Exception nếu như cung cấp thông tin RegisteredClient không đúng và được redirect đến /login)
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                // Bỏ qua CSRF cho các endpoint OAuth2
                .csrf(csrf -> csrf.ignoringRequestMatchers(authorizationServerConfigurer.getEndpointsMatcher()));

        // redirect user chưa login
        http.exceptionHandling(ex ->
                ex.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
        );
        return http.build();
    }

    private static final String[] AUTH_SECURITY_MATCHERS = {
            "/auth/public/**",
            "/login/**",
            "/oauth2/**",
            "/error"
    };

    @Bean
    @Order(2)
    public SecurityFilterChain authenticationSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                // 1. Áp dụng FilterChain cho các pattern công khai
                .securityMatcher(AUTH_SECURITY_MATCHERS)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AUTH_SECURITY_MATCHERS).permitAll()
                )
                .formLogin(Customizer.withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers(AUTH_SECURITY_MATCHERS));

        return http.build();
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .formLogin(Customizer.withDefaults());
        return http.build();
    }
}
