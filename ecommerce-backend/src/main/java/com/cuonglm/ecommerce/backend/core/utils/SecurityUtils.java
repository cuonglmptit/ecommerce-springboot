package com.cuonglm.ecommerce.backend.core.utils;

import com.cuonglm.ecommerce.backend.core.exception.UnauthenticatedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

/**
 * SecurityUtils – Utils dùng cho thông tin người dùng.
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

    /**
     * Lấy ra ID người dùng hiện tại hoặc {@link Optional#empty()}.
     * @return
     */
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

    /**
     * Lấy ra ID người dùng hoặc throw luôn lỗi chưa xác thực
     * @return ID người người dùng hiện tại hoặc ném lỗi
     */
    public static Long getRequiredCurrentUserId() {
        return getCurrentUserId()
                .orElseThrow(() -> new UnauthenticatedException("Người dùng chưa đăng nhập hoặc Token không hợp lệ."));
    }
}
