package com.cuonglm.ecommerce.backend.core.exception;

/**
 /**
 * BaseRuntimeException – Lớp cha cho tất cả các Exception nghiệp vụ (Business) tùy chỉnh.
 * <p>
 * Kế thừa RuntimeException để trở thành Unchecked Exception, đơn giản hóa việc xử lý lỗi.
 * </p>
 *
 * @author cuonglmptit
 * @since Thursday, 20 November 2025
 */
public abstract class BaseRuntimeException extends RuntimeException {
    protected BaseRuntimeException(String message) {
        super(message);
    }

    protected BaseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
