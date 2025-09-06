package com.cuonglm.ecommerce.backend.auth.model;

import com.cuonglm.ecommerce.backend.user.dto.UserSecurityAndProfileDTO;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * BasePrincipal – Lớp interface chung cho các Principal trong hệ thống như UserDetails và OAuth2User.
 *
 * <p>
 * Dù đăng nhập qua formLogin hay OAuth2/Oidc, cả hai đều dùng chung {@link UserSecurityAndProfileDTO}.
 * <br>
 * Nên các lớp Principal cụ thể sẽ triển khai interface này để cung cấp thông tin
 * phục vụ cho việc issue JWT Token dựa trên thông tin của người dùng.
 * </p>
 *
 * @author cuonglmptit
 * @since Saturday, 01 November 2025
 */
public interface BasePrincipal {
    /**
     * @return UserSecurityAndProfileDTO liên quan đến Principal này.
     */
    UserSecurityAndProfileDTO getUserSecurityAndProfileDTO();

    // Các phương thức tiện ích (Giúp tránh gọi .getUserSecurityDTO().getXXX() nhiều lần)

    /**
     * @return ID của người dùng trong DB (Dùng làm JWT Subject).
     */
    Long getId();

    /**
     * @return Tên người dùng / Username (Dùng làm JWT Claim).
     */
    String getUsername();

    /**
     * @return Email của người dùng (Dùng làm JWT Claim).
     */
    String getEmail();

    /**
     * @return Toàn bộ quyền hạn (Roles/Permissions) của người dùng.
     */
    Collection<? extends GrantedAuthority> getAuthorities();

    /**
     * @return URL avatar của người dùng.
     */
    String getAvatarUrl();

    /**
     * @return Tên đầy đủ của người dùng.
     */
    String getFullName();
}
