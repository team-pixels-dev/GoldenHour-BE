package com.example.goldenhour.security.config;

import com.example.goldenhour.security.oauth2.GoogleOAuth2UserInfoProxy;
import com.example.goldenhour.security.oauth2.OAuth2UserInfoProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class OAuth2UserInfoProxyConfig {

    @Bean
    public Map<String, OAuth2UserInfoProxy> oAuth2UserInfoProxyMap() {

        Map<String, OAuth2UserInfoProxy> oAuth2UserInfoProxyMap = new HashMap<>();

        oAuth2UserInfoProxyMap.put("google", new GoogleOAuth2UserInfoProxy());

        return oAuth2UserInfoProxyMap;
    }
}
