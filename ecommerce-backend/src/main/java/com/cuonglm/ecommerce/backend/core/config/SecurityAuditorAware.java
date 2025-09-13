package com.cuonglm.ecommerce.backend.core.config;

import com.cuonglm.ecommerce.backend.auth.model.BasePrincipal;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * SecurityAuditorAware – Cung cấp thông tin người dùng (User Entity) hiện tại cho JPA Auditing.
 *
 * <p>
 * Trích xuất User Principal từ Spring Security Context để điền vào các trường @CreatedBy và @LastModifiedBy.
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 07 November 2025
 */
@Component
public class SecurityAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        // 1. Lấy đối tượng Authentication từ Spring Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Kiểm tra nếu không có xác thực (ví dụ: tác vụ nền) hoặc chưa được xác thực
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of(1L);
        }

        // 3. Lấy Principal (Đối tượng đại diện cho người dùng)
        Object principal = authentication.getPrincipal();

        if (principal instanceof BasePrincipal user) {
            return Optional.of(user.getId());
        }

        // Nếu như không có thì trả về ID 0
        return Optional.of(1L);
    }
}