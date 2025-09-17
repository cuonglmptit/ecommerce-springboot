package com.cuonglm.ecommerce.backend.auth.config.resourceserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * JwtConverterConfig – Lớp converter cho scope và roles khi nhận Jwt Token từ Auth Server.
 *
 * <p>
 * Logic chuyển đổi các trường roles và scope trong Jwt Token thành GrantedAuthority
 * để Spring Security có thể sử dụng .hasAuthoriry(), .hasRole()... trong việc phân quyền truy cập tài nguyên.
 * </p>
 *
 * @author cuonglmptit
 * @since Thursday, 23 October 2025
 */
@Configuration
public class JwtAuthenticationConverterConfig {
    @Bean
    public JwtAuthenticationConverter customJwtAuthenticationConverter() {
        // 1. Khởi tạo Converter cho Scopes
        // Sẽ đọc claim 'scope' hoặc 'scp' và thêm tiền tố 'SCOPE_'
        JwtGrantedAuthoritiesConverter scopeAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // 2. Khởi tạo Converter cho Roles (Cấu hình tùy chỉnh)
        JwtGrantedAuthoritiesConverter rolesAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // JwtGrantedAuthoritiesConverter mặc định tìm kiếm claim là "scope" hoặc "scp"
        // và map ra authorities với prefix "SCOPE_" nên cần khai báo rõ claim name và tiền tố cho roles
        rolesAuthoritiesConverter.setAuthoritiesClaimName("roles"); // Đọc từ claim "roles"
        rolesAuthoritiesConverter.setAuthorityPrefix("ROLE_");       // Thêm tiền tố "ROLE_"

        // Khởi tạo Converter chính
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        // 3. Ánh xạ Chính: Gọi từng converter và kết hợp kết quả
        converter.setJwtGrantedAuthoritiesConverter(
                jwt -> {
                    // Lấy Authorities từ Scopes (ví dụ: SCOPE_read)
                    // JwtGrantedAuthoritiesConverter mặc định tìm kiếm claim là "scope" hoặc "scp" và map ra authorities với prefix "SCOPE_"
                    Collection<GrantedAuthority> scopeAuthorities = scopeAuthoritiesConverter.convert(jwt);

                    // Lấy Authorities từ Roles (ví dụ: ROLE_ADMIN)
                    Collection<GrantedAuthority> roleAuthorities = rolesAuthoritiesConverter.convert(jwt);

                    // Kết hợp cả hai danh sách vào một Set duy nhất để loại bỏ trùng lặp (nếu có)
                    Set<GrantedAuthority> authorities = new HashSet<>();
                    authorities.addAll(scopeAuthorities);
                    authorities.addAll(roleAuthorities);
                    return authorities;
                }
        );
        return converter;
    }
}
