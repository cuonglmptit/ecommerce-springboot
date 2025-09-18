package com.cuonglm.ecommerce.backend.auth.model;

import com.cuonglm.ecommerce.backend.user.dto.internal.UserSecurityAndProfileDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * AbstractBasePrincipal – Lớp trừu tượng cơ sở cho các Principal, triển khai logic chung của {@link BasePrincipal}.
 *
 * <p>
 * Đảm bảo mọi Principal ({@link LocalUserPrincipal}, {@link CustomOAuth2Principal}, {@link CustomOidcPrincipal})
 * có thể truy cập thông tin cốt lõi (ID, Roles)
 * từ {@link UserSecurityAndProfileDTO} một cách thống nhất.
 * Các lớp Principal con sẽ kế thừa để tránh lặp lại logic và getters tiện ích.
 * </p>
 *
 * @author cuonglmptit
 * @since Sunday, 02 November 2025
 */
public abstract class AbstractBasePrincipal implements BasePrincipal {
    // Protected UserSecurityAndProfileDTO để các lớp con có thể truy cập trực tiếp
    protected final UserSecurityAndProfileDTO userSecurityAndProfileDTO;

    public AbstractBasePrincipal(UserSecurityAndProfileDTO userSecurityAndProfileDTO) {
        this.userSecurityAndProfileDTO = userSecurityAndProfileDTO;
    }

    //<editor-fold defaultstate="collapsed" desc="Các phương thức của BasePrincipal">
    // Triển khai sẵn các phương thức của BasePrincipal để tránh lặp lại trong các lớp con
    @Override
    public UserSecurityAndProfileDTO getUserSecurityAndProfileDTO() {
        return this.userSecurityAndProfileDTO;
    }

    @Override
    public Long getId() {
        return userSecurityAndProfileDTO.getId();
    }

    @Override
    public String getUsername() {
        return userSecurityAndProfileDTO.getUsername();
    }

    @Override
    public String getEmail() {
        return userSecurityAndProfileDTO.getEmail();
    }

    @Override
    public String getAvatarUrl() {
        return userSecurityAndProfileDTO.getAvatarUrl();
    }

    @Override
    public String getFullName() {
        return userSecurityAndProfileDTO.getFullName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userSecurityAndProfileDTO.getAuthorities().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }
    //</editor-fold>
}
