package com.cuonglm.ecommerce.backend.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JpaAuditingConfig – Cấu hình tự động kích hoạt tính năng JPA Auditing (@CreatedDate, @CreatedBy...).
 *
 * @author cuonglmptit
 * @since Sunday, 16 August 2026
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "securityAuditorAware")
public class JpaAuditingConfig {
}