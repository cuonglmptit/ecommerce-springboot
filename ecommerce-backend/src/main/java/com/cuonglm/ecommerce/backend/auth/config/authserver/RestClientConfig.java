package com.cuonglm.ecommerce.backend.auth.config.authserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * RestClientConfig – Lớp config RestClient.
 *
 * <p>
 * Trả về 1 @Bean RestClient.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 29 October 2025
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }
}