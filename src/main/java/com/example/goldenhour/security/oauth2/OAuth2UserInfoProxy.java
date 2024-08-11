package com.example.goldenhour.security.oauth2;

import com.example.goldenhour.dto.OAuth2Response;

public interface OAuth2UserInfoProxy {

    /**
     * OAuth2 인증 서버에 액세스 토큰으로 요청을 보내서 유저 정보 획득 & 반환
     * @param accessToken
     * @return 유저 정보를 담은 OAuth2Response
     */
    public OAuth2Response getOAuth2UserInfo(String accessToken);
}
