package com.cuonglm.ecommerce.backend.auth.config.resourceserver;

import com.cuonglm.ecommerce.backend.auth.config.keys.PemUtils;
import com.cuonglm.ecommerce.backend.auth.config.keys.RsaKeyProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.interfaces.RSAPublicKey;

/**
 * JwtDecoderConfig – Lớp config cho việc Decode/Verify xem Jwt từ Auth Server có legit không.
 *
 * <p>
 * Cấu hình cho Decode/Verify Jwt hiện tại,
 * sau này tách ra Resource Server riêng thì cách nên dùng là tự động cấu hình trong <br>
 * <i>
 * applications.properties:
 * </i>
 * <br>
 * spring.security.oauth2.resourceserver.jwt.issuer-uri: http://auth-server-host:port
 * <br>
 * Hoặc cần tùy chỉnh cấu hình thủ công thì có thể dùng class này.
 * Hiện tại đang dùng thủ công để load thẳng RsaKeyProperties từ /resources/certs/public.pem
 * </p>
 *
 * @author cuonglmptit
 * @since Thursday, 23 October 2025
 */
@Configuration
public class JwtDecoderConfig {
//    private final RsaKeyProperties jwtRsaKeyProperties;
//    private final PemUtils pemUtils;
    private final String issuerUri;

    public JwtDecoderConfig(RsaKeyProperties jwtRsaKeyProperties, PemUtils pemUtils, @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri) {
//        this.jwtRsaKeyProperties = jwtRsaKeyProperties;
//        this.pemUtils = pemUtils;
        this.issuerUri = issuerUri;
    }
    /* Chưa cần đoạn code này vì hiên tại ResourceServer đang chung với Auth Server -> sau này tách ra thì cần
    / vì lúc đó ResourceServer chỉ cần public key
    @Bean
    public RSAPublicKey rsaPublicKey() throws Exception {
        return pemUtils.parsePublicKey(jwtRsaKeyProperties.rsaPublicKey());
    }
    */

    @Bean
    // Hện tại đang chung server nên chỉ cần tự động inject bean vào
    public JwtDecoder jwtDecoder(RSAPublicKey rsaPublicKey) {
        // 1. Tạo JwtDecoder từ Public Key
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
        // 2. Thêm Default Validator
        // Vẫn cần xác minh Issuer vì có thể Hacker tự tạo token với chữ ký đúng nhưng
        // issuer sai (không phải AuthServer của mình phát hành) để truy cập vào Resource Server
        OAuth2TokenValidator<Jwt> defaultValidators = JwtValidators.createDefaultWithIssuer(issuerUri);
        jwtDecoder.setJwtValidator(defaultValidators); // Gán Validator
        //... Có thể thêm các Validator tùy chỉnh khác nếu cần

        // Trả về JwtDecoder đã cấu hình
        return jwtDecoder;
    }
}
