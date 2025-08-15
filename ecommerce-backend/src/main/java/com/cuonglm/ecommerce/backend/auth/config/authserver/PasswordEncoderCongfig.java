package com.cuonglm.ecommerce.backend.auth.config.authserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * PasswordEncoderCongfig – Lớp config cho mã hóa mật khẩu.
 *
 * <p>
 * Cấu hình trong ứng dụng này sẽ sử dụng BCryptPasswordEncoder để mã hóa mật khẩu người dùng.
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 17 October 2025
 */
@Configuration
public class PasswordEncoderCongfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        //Sử dụng BCryptPasswordEncoder để mã hóa mật khẩu
        return new BCryptPasswordEncoder();
    }
}
