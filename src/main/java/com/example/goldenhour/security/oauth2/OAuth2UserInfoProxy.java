package com.example.goldenhour.security.oauth2;

import com.example.goldenhour.dto.OAuth2Response;

public interface OAuth2UserInfoProxy {

    public OAuth2Response getOAuth2UserInfo(String accessToken);
}
