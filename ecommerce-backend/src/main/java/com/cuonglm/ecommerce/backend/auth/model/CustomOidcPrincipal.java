package com.cuonglm.ecommerce.backend.auth.model;

import com.cuonglm.ecommerce.backend.auth.service.oauth2.CustomOidcUserService;
import com.cuonglm.ecommerce.backend.user.dto.internal.UserSecurityAndProfileDTO;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Map;


/**
 * CustomOidcPrincipal – Triển khai {@link OidcUser} cho luồng đăng nhập bằng Oidc chuẩn.
 *
 * <p>
 * Cung cấp thông tin người dùng cần thiết cho việc issue JWT Token. <br>
 * Triển khai {@link OidcUser} để tương thích với Spring Security,
 * đồng thời triển khai {@link BasePrincipal} để unify luồng xử lý issue JWT. <br>
 * Được sử dụng khi đăng nhập bằng chuẩn OpenID Connect (OIDC) như Google,
 * được dùng ở lớp {@link CustomOidcUserService}
 * </p>
 *
 * @author cuonglmptit
 * @since Saturday, 01 November 2025
 */
public class CustomOidcPrincipal extends AbstractBasePrincipal implements OidcUser {
    // Delegate OidcUser để tận dụng các phương thức đã có sẵn
    private final OidcUser oidcUserDelegate;

    public CustomOidcPrincipal(UserSecurityAndProfileDTO userSecurityAndProfileDTO, OidcUser oidcUserDelegate) {
        super(userSecurityAndProfileDTO);
        this.oidcUserDelegate = oidcUserDelegate;
    }

    //<editor-fold desc="Các cài đặt của OidcUser thông qua delegate">
    @Override
    public Map<String, Object> getClaims() {
        return oidcUserDelegate.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUserDelegate.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return oidcUserDelegate.getIdToken();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oidcUserDelegate.getAttributes();
    }

    @Override
    public String getName() {
        // 1. Luôn ưu tiên ID DB Local (ID ổn định và duy nhất trong hệ thống của bạn)
        if (userSecurityAndProfileDTO.getId() != null) {
            return userSecurityAndProfileDTO.getId().toString();
        }
        // 2. Chỉ dùng ID bên ngoài nếu ID DB chưa tồn tại (ví dụ: ngay sau khi đăng ký lần đầu)
        return oidcUserDelegate.getName();
    }
    //</editor-fold>
}
