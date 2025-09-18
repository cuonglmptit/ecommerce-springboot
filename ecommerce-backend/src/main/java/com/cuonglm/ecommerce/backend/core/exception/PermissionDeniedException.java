package com.cuonglm.ecommerce.backend.core.exception;

/**
 * PermissionDeniedException – Lỗi không có quyền truy cập/thực hiện.
 *
 * <p>
 * Map tới HTTP Status 403 (Forbidden).
 * </p>
 *
 * @author cuonglmptit
 * @since Thursday, 20 November 2025
 */
public class PermissionDeniedException extends BaseRuntimeException {
    public PermissionDeniedException(String message) {
        super(message);
    }

    public PermissionDeniedException() {
        super("Bạn không có quyền truy cập hoặc thực hiện hành động này.");
    }
}
