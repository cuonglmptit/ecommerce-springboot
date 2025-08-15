package com.cuonglm.ecommerce.backend.auth.config.keys;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RsaKeyProperties – Cặp khóa phục vụ việc sign token và verify token.
 *
 * <p>
 * Lấy cặp khóa bất đối xứng từ biến môi trường (.env) hoặc từ cặp khóa được sinh ra trong resources/certs.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 22 October 2025
 */
@ConfigurationProperties(prefix = "auth.jwt")
public record RsaKeyProperties(String rsaPublicKey, String rsaPrivateKey) {
}
