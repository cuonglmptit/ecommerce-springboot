package com.cuonglm.ecommerce.backend.auth.service.oauth2.github;

import com.cuonglm.ecommerce.backend.auth.service.oauth2.AbstractUserOAuth2Info;
import com.cuonglm.ecommerce.backend.user.enums.OAuth2Provider;

import java.util.Map;

/**
 * GithubUserOAuth2Info – Ánh xạ thuộc tính của Github.
 *
 * @author cuonglmptit
 * @since Saturday, 01 November 2025
 */
public class GithubUserOAuth2Info extends AbstractUserOAuth2Info {

    public GithubUserOAuth2Info(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProviderUserId() {
        // ID của GitHub là số, cần đảm bảo trả về String
        Long id = getLongAttribute("id");
        return id != null ? String.valueOf(id) : null;
    }

    @Override
    public OAuth2Provider getProvider() {
        return OAuth2Provider.GITHUB;
    }

    @Override
    public String getEmail() {
        // Giả sử logic lấy email (từ GitHubEmailFetcher) đã được đưa vào attributes
        return getStringAttribute("email");
    }

    @Override
    public String getFullName() {
        // GitHub có thể dùng 'name' (Tên thật) hoặc 'login' (Username)
        return getStringAttribute("name") != null
                ? getStringAttribute("name")
                : getStringAttribute("login");
    }

    @Override
    public String getAvatarUrl() {
        return getStringAttribute("avatar_url");
    }
}