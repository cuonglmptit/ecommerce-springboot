package com.cuonglm.ecommerce.backend.auth.model;

import com.cuonglm.ecommerce.backend.user.dto.UserSecurityAndProfileDTO;
import com.cuonglm.ecommerce.backend.user.enums.UserStatus;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * LocalUserPrincipal – Lớp triển khai UserDetails cho Spring Security hiểu,
 * về cơ bản nó là một Principal cho luồng đăng nhập qua formLogin.
 *
 * <p>
 * Lớp này được sử dụng để cung cấp thông tin người dùng cho Spring Security,
 * bao gồm tên người dùng, mật khẩu, quyền hạn và các thông tin khác cần thiết
 * để xác thực và phân quyền người dùng trong ứng dụng.
 * <br>
 * Nó sẽ được sử dụng trong quá trình xác thực người dùng và kiểm tra quyền truy cập
 * vào các tài nguyên trong ứng dụng.
 * </p>
 *
 * @author cuonglmptit
 * @since Thursday, 14 August 2025
 */
public class LocalUserPrincipal extends AbstractBasePrincipal implements UserDetails {
    // Constructor nhận vào UserSecurityAndProfileDTO
    public LocalUserPrincipal(UserSecurityAndProfileDTO userSecurityAndProfileDTO) {
        super(userSecurityAndProfileDTO);
    }

    //<editor-fold defaultstate="collapsed" desc="Các phương thức của UserDetails">
    @Override
    public String getPassword() {
        return userSecurityAndProfileDTO.getPasswordHash();
    }

    @Override
    public boolean isAccountNonExpired() {
        return userSecurityAndProfileDTO.getStatus() != UserStatus.DELETED;
    }

    @Override
    public boolean isAccountNonLocked() {
        UserStatus status = userSecurityAndProfileDTO.getStatus();
        // Tài khoản bị khóa, bị ban, hoặc bị tạm ngừng đều bị coi là "Locked" (hoặc không hợp lệ)
        return status != UserStatus.SUSPENDED && status != UserStatus.BANNED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Chỉ ACTIVE mới được phép đăng nhập.
        // Mọi trạng thái khác (PENDING, DEACTIVATED, DELETED) sẽ bị chặn bởi DisabledException.
        return userSecurityAndProfileDTO.getStatus() == UserStatus.ACTIVE;
    }
    //</editor-fold>

}
