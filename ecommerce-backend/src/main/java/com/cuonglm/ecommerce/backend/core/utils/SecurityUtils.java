package com.cuonglm.ecommerce.backend.core.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SecurityUtils – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 19 November 2025
 */
public class SecurityUtils {

    public static Optional<String> getCurrentUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        // 1. Trường hợp Resource Server (JWT)
        if (principal instanceof Jwt jwt) {
            // Ưu tiên lấy username
            if (jwt.hasClaim("username")) {
                return Optional.ofNullable(jwt.getClaimAsString("username"));
            }
            // Nếu không, lấy email
            if (jwt.hasClaim("email")) {
                return Optional.ofNullable(jwt.getClaimAsString("email"));
            }
            // Cuối cùng mới lấy subject (thường là ID: "3")
            return Optional.ofNullable(jwt.getSubject());
        }
        return Optional.empty();
    }

    public static Optional<Long> getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            try {
                return Optional.of(Long.valueOf(jwt.getSubject()));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
