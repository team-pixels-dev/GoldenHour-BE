package com.example.goldenhour.security.jwt;

import com.example.goldenhour.dto.OAuth2Response;

public interface OAuth2UserInfoProxy {

    public OAuth2Response getOAuth2UserInfoByAccessToken(String accessToken);
}
