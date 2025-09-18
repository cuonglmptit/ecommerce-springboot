package com.cuonglm.ecommerce.backend.auth.service.userdetail;


import com.cuonglm.ecommerce.backend.auth.model.LocalUserPrincipal;
import com.cuonglm.ecommerce.backend.auth.service.common.AuthUserHandler;
import com.cuonglm.ecommerce.backend.user.dto.internal.UserSecurityAndProfileDTO;
import com.cuonglm.ecommerce.backend.user.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * CustomUserDetailsManager – Lớp cài đặt cho UserDetailsManager
 *
 * <p>
 * Lớp này cài đặt cho UserDetailsManager (UserDetailsManager mở rộng của UserDetailsService)
 * nên sẽ dùng để thay thế cho UserDetailsService để SpringSecurity lấy ra được user cho Filter..
 * </p>
 *
 * @author cuonglmptit
 * @since Tuesday, 14 October 2025
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {
    // Sử dụng UserService để lấy user từ DB
    private final UserService userService;
    private final AuthUserHandler authUserHandler;

    public CustomUserDetailsService(UserService userService, AuthUserHandler authUserHandler) {
        this.userService = userService;
        this.authUserHandler = authUserHandler;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Tìm User (Nếu không tìm thấy, ném UsernameNotFoundException)
        UserSecurityAndProfileDTO userDTO = userService.findSecurityDetailsByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + username));
        // 2. Kiểm tra trạng thái của người dùng
        userDTO = authUserHandler.checkAndHandleUserStatus(userDTO);
        // 3. Trả về Principal dạng LocalUserPrincipal
        return new LocalUserPrincipal(userDTO);
    }

}
