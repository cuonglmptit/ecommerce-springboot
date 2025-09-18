package com.cuonglm.ecommerce.backend.auth.service.common;

import com.cuonglm.ecommerce.backend.user.dto.internal.UserSecurityAndProfileDTO;
import com.cuonglm.ecommerce.backend.user.enums.UserStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * AuthUserHandler – Lớp xử lý trung tâm cho việc xác thực trạng thái tài khoản.
 *
 * <p>
 * Lớp này chịu trách nhiệm kiểm tra trạng thái người dùng (UserStatus) và ném ra
 * các ngoại lệ (AuthenticationException) chuẩn của Spring Security (hoặc tùy chỉnh)
 * khi người dùng không ở trạng thái ACTIVE. Việc tập trung logic này giúp các Service
 * đăng nhập (Form, Oauth2, OIDC) trở nên Agnostic và gọn gàng hơn.
 * </p>
 *
 * @author cuonglmptit
 * @since Monday, 10 November 2025
 */
@Component
public class AuthUserHandler {

    /**
     * Kiểm tra và đảm bảo người dùng đang ở trạng thái ACTIVE (Hoạt động).
     *
     * <p>
     * Nếu trạng thái không phải ACTIVE, phương thức sẽ ném ra một AuthenticationException
     * tương ứng, chặn luồng cấp Token hoặc xác thực.
     * </p>
     *
     * @param userDTO DTO chứa thông tin bảo mật của người dùng local.
     * @return {@code userDTO} nếu trạng thái là ACTIVE.
     * @throws AuthenticationException Nếu người dùng không ở trạng thái ACTIVE.
     */
    public UserSecurityAndProfileDTO checkAndHandleUserStatus(UserSecurityAndProfileDTO userDTO)
            throws AuthenticationException {

        if (userDTO.getStatus() != UserStatus.ACTIVE) {
            // Logic xử lý lỗi được tập trung tại đây
            throw mapStatusToAuthenticationException(userDTO.getStatus());
        }
        return userDTO;
    }

    /**
     * Ánh xạ một trạng thái người dùng {@link UserStatus} không hợp lệ
     * sang một ngoại lệ bảo mật {@link AuthenticationException} phù hợp và trả về để có thể throw hoặc xử lý khác.
     *
     * @param status Trạng thái người dùng khác {@link UserStatus#ACTIVE}.
     * @return AuthenticationException đối tượng ngoại lệ đã được tạo.
     */
    private AuthenticationException mapStatusToAuthenticationException(UserStatus status) {
        final String message;
        switch (status) {
            case DEACTIVATED:
                // Người dùng tự vô hiệu hóa
                message = "Tài khoản của bạn đã bị vô hiệu hóa.";
                return new DisabledException(message);

            case SUSPENDED:
                // Bị khóa tạm thời (thường do Admin/hệ thống khóa)
                message = "Tài khoản của bạn đã bị khóa tạm thời.";
                return new LockedException(message);

            default: // BANNED, DELETED (Xử lý như bị vô hiệu hóa)
                message = "Tài khoản của bạn đã bị khóa hoặc không còn tồn tại.";
                return new DisabledException(message);
        }
    }
}