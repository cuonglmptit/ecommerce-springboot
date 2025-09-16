package com.cuonglm.ecommerce.backend.auth.service.oauth2.google;

import com.cuonglm.ecommerce.backend.auth.service.oauth2.AbstractUserOAuth2Info;
import com.cuonglm.ecommerce.backend.user.enums.OAuth2Provider;

import java.util.Map;

/**
 * GoogleUserOAuth2Info – Ánh xạ thuộc tính của Google.
 *
 * @author cuonglmptit
 * @since Friday, 31 October 2025
 */
public class GoogleUserOAuth2Info extends AbstractUserOAuth2Info {

    public GoogleUserOAuth2Info(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProviderUserId() {
        return getStringAttribute("sub"); // Google sử dụng "sub"
    }

    @Override
    public OAuth2Provider getProvider() {
        return OAuth2Provider.GOOGLE;
    }

    @Override
    public String getEmail() {
        return getStringAttribute("email");
    }

    @Override
    public String getFullName() {
        return getStringAttribute("name");
    }

    @Override
    public String getAvatarUrl() {
        return getStringAttribute("picture");
    }
}