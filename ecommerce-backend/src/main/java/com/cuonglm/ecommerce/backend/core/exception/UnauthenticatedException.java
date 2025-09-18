package com.cuonglm.ecommerce.backend.core.exception;

/**
 * UnauthenticatedException – Lỗi chưa được xác thực (chưa đăng nhập/token hết hạn).
 *
 * <p>
 * Map tới HTTP Status 401 (Unauthorized).
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 19 November 2025
 */
public class UnauthenticatedException extends BaseRuntimeException {
    public UnauthenticatedException(String message) {
        super(message);
    }

    public UnauthenticatedException() {
        super("Yêu cầu xác thực. Bạn cần đăng nhập để truy cập tài nguyên này.");
    }
}
