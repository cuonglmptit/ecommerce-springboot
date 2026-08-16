package com.cuonglm.ecommerce.backend.core.config;

import com.cuonglm.ecommerce.backend.core.constants.CoreConstants;
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
        return SecurityUtils.getCurrentUserId()
                .or(() -> Optional.of(CoreConstants.SYSTEM_USER_ID));
    }
}