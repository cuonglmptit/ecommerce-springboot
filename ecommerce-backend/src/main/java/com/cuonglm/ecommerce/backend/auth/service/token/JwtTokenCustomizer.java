package com.cuonglm.ecommerce.backend.auth.service.token;

import com.cuonglm.ecommerce.backend.auth.model.BasePrincipal;
import com.cuonglm.ecommerce.backend.auth.model.LocalUserPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

/**
 * <p>
 * JwtTokenCustomizer - Lớp này triển khai giao diện {@link OAuth2TokenCustomizer} để tùy chỉnh nội dung (claims) của
 * JSON Web Token (JWT) do Spring Authorization Server phát hành.
 * </p>
 *
 * <p>
 * Mục đích chính là thêm các thông tin quan trọng của người dùng đã xác thực (như Roles và
 * Username,...) vào JWT Access Token. Điều này cho phép Resource Server (API) thực hiện
 * phân quyền (Authorization) một cách hiệu quả mà không cần gọi lại Authorization Server.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 22 October 2025
 */
@Component
public class JwtTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {
    @Override
    public void customize(JwtEncodingContext context) {
        if ("access_token".equals(context.getTokenType().getValue())) {
            var authentication = context.getPrincipal(); // Lấy đối tượng Authentication
            var principalObject = authentication.getPrincipal();  // Lấy Principal thực sự

            // 1. Lấy roles trực tiếp từ Authentication và thêm vào JWT claim (cho mọi trường hợp)
            var roles = authentication.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .toList();

            // Thêm roles vào JWT claim
            context.getClaims().claim("roles", roles);

            // 2. Thêm thông tin mình custom vào JWT token
            if (principalObject instanceof BasePrincipal basePrincipal) {

                if (basePrincipal.getId() != null) {
                    context.getClaims().subject(basePrincipal.getId().toString());
                }

                // Thêm các Claims còn lại qua Utility Method
                addClaimIfNotNull(context, "username", basePrincipal.getUsername());
                addClaimIfNotNull(context, "email", basePrincipal.getEmail());
                addClaimIfNotNull(context, "avatar_url", basePrincipal.getAvatarUrl());
                addClaimIfNotNull(context, "full_name", basePrincipal.getFullName());
            }
        }
    }

    // Phương thức tiện ích để thêm claim nếu giá trị không null
    private void addClaimIfNotNull(JwtEncodingContext context, String claimName, Object claimValue) {
        if (claimValue != null) {
            context.getClaims().claim(claimName, claimValue);
        }
    }
}
