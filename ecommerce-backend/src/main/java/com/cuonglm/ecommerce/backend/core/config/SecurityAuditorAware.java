package com.cuonglm.ecommerce.backend.core.config;

import com.cuonglm.ecommerce.backend.core.utils.SecurityUtils;
import org.springframework.data.domain.AuditorAware;
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
        Optional<Long> currentUserId = SecurityUtils.getCurrentUserId();

        // Nếu người dùng tồn tại, trả về ID đó.
        // Nếu không tồn tại (Optional.empty()), trả về Optional.of(1L) làm ID mặc định (SystemAuditor).
        if (currentUserId.isPresent()) {
            return currentUserId;
        } else {
            // ID 1L là ID của người dùng hệ thống
            return Optional.of(1L);
        }
    }
}