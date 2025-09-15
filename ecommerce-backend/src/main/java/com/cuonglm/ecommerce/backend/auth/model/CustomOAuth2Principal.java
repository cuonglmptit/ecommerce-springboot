package com.cuonglm.ecommerce.backend.auth.model;

import com.cuonglm.ecommerce.backend.auth.service.oauth2.CustomOAuth2UserService;
import com.cuonglm.ecommerce.backend.user.dto.UserSecurityAndProfileDTO;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

/**
 * CustomOAuth2Principal – Lớp triển khai {@link OAuth2User} cho luồng đăng nhập bằng OAuth2
 *
 * <p>
 * Cung cấp thông tin người dùng cần thiết cho việc issue JWT Token. <br>
 * Triển khai {@link OAuth2User} để tương thích với Spring Security,
 * đồng thời triển khai {@link BasePrincipal} để unify luồng xử lý issue JWT. <br>
 * Được sử dụng khi đăng nhập bằng Oauth2 như GitHub (không phải OIDC chuẩn),
 * được dùng ở lớp {@link CustomOAuth2UserService}
 * </p>
 *
 * @author cuonglmptit
 * @since Saturday, 01 November 2025
 */
public class CustomOAuth2Principal extends AbstractBasePrincipal implements OAuth2User {

    private final OAuth2User oauth2UserDelegate;

    public CustomOAuth2Principal(UserSecurityAndProfileDTO userSecurityAndProfileDTO, OAuth2User oauth2UserDelegate) {
        super(userSecurityAndProfileDTO);
        this.oauth2UserDelegate = oauth2UserDelegate;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2UserDelegate.getAttributes();
    }

    @Override
    public String getName() {
        // 1. Luôn ưu tiên ID DB Local (ID ổn định và duy nhất trong hệ thống của bạn)
        if (userSecurityAndProfileDTO.getId() != null) {
            return userSecurityAndProfileDTO.getId().toString();
        }
        // 2. Chỉ dùng ID bên ngoài nếu ID DB chưa tồn tại (ví dụ: ngay sau khi đăng ký lần đầu)
        return oauth2UserDelegate.getName();
    }
}