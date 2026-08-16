package com.cuonglm.ecommerce.backend.core.constants;

/**
 * CoreConstants – Tập trung các hằng số dùng chung cho toàn bộ hệ thống.
 *
 * @author cuonglmptit
 * @since Sunday, 16 August 2026
 */
public final class CoreConstants {
    private CoreConstants() {}

    // ID người dùng hệ thống (dùng cho background job, seed data, batch)
    public static final Long SYSTEM_USER_ID = 0L;

    // Headers & MDC Tracing
    public static final String HEADER_REQUEST_ID = "X-Request-ID";
    public static final String MDC_KEY_REQUEST_ID = "requestId";

    // Phân trang mặc định
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
}