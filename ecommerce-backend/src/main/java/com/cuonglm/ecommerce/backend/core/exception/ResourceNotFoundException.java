package com.cuonglm.ecommerce.backend.core.exception;

/**
 * ResourceNotFoundException – Lỗi không tìm thấy tài nguyên.
 * <p>
 * Map tới HTTP Status 404 (Not Found).
 * </p>
 *
 * @author cuonglmptit
 * @since Tuesday, 18 November 2025
 */
public class ResourceNotFoundException extends BaseRuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Constructor tiện ích
    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s với ID %d không tìm thấy.", resourceName, id));
    }
}