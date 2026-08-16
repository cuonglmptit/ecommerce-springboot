package com.cuonglm.ecommerce.backend.core.filter;

import com.cuonglm.ecommerce.backend.core.constants.CoreConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * RequestCorrelationFilter – Bắt hoặc tự sinh X-Request-ID để phục vụ truy vết Log toàn hệ thống.
 *
 * @author cuonglmptit
 * @since Sunday, 16 August 2026
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestCorrelationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestId = request.getHeader(CoreConstants.HEADER_REQUEST_ID);

        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        try {
            MDC.put(CoreConstants.MDC_KEY_REQUEST_ID, requestId);
            response.setHeader(CoreConstants.HEADER_REQUEST_ID, requestId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(CoreConstants.MDC_KEY_REQUEST_ID);
        }
    }
}