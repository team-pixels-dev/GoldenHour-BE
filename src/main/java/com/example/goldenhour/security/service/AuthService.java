package com.example.goldenhour.security.service;

import com.example.goldenhour.dto.OAuth2Response;
import com.example.goldenhour.dto.UserDTO;
import com.example.goldenhour.entity.UserEntity;
import com.example.goldenhour.exception.http.Http400ResponseException;
import com.example.goldenhour.repository.UserRepository;
import com.example.goldenhour.security.dto.LoginRequestDTO;
import com.example.goldenhour.security.jwt.JWTUtil;
import com.example.goldenhour.security.oauth2.OAuth2UserInfoProxy;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final Map<String, OAuth2UserInfoProxy> oAuth2UserInfoProxyMap;
    private final JWTUtil jwtUtil;

    public UserDTO login(LoginRequestDTO loginRequestDTO, String providerName) {

        if (!oAuth2UserInfoProxyMap.containsKey(providerName)) {

            throw new Http400ResponseException("지원하지 않는 소셜 로그인입니다.");
        }

        OAuth2UserInfoProxy oAuth2UserInfoProxy = oAuth2UserInfoProxyMap.get(providerName);
        OAuth2Response oAuth2Response = oAuth2UserInfoProxy.getOAuth2UserInfo(loginRequestDTO.getAccessToken());

        if (oAuth2Response == null) {

            throw new Http400ResponseException("oAuth2Response가 null입니다.");
        }

        UserEntity userEntity = getUserEntityByUserId(loginRequestDTO.getUserId());

        if (checkLoginUser(loginRequestDTO, oAuth2Response)) {

            throw new Http400ResponseException("유효하지 않은 사용자입니다.");
        }

        if (userEntity == null) {

            userEntity = join(loginRequestDTO, oAuth2Response);
        }


        return makeUserDto(userEntity);
    }

    private UserEntity getUserEntityByUserId(String userId) {

        return userRepository.findByUsername(userId);
    }

    private boolean checkLoginUser(LoginRequestDTO loginRequestDTO, OAuth2Response oAuth2Response) {

        String userId = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        return (loginRequestDTO.getUserId().equals(userId));
    }

    private UserEntity join(LoginRequestDTO loginRequestDTO, OAuth2Response oAuth2Response) {
        UserEntity userEntity = makeUserEntity(loginRequestDTO, oAuth2Response);
        userRepository.save(userEntity);
        return userEntity;
    }

    private UserEntity makeUserEntity(LoginRequestDTO loginRequestDTO, OAuth2Response oAuth2Response) {
        UserEntity userEntity;
        userEntity = new UserEntity();
        userEntity.setUsername(oAuth2Response.getProvider() + " " + loginRequestDTO.getUserId());
        userEntity.setEmail(oAuth2Response.getEmail());
        userEntity.setName(oAuth2Response.getName());
        userEntity.setRole("ROLE_USER");

        return userEntity;
    }

    private UserDTO makeUserDto(UserEntity userEntity) {

        UserDTO userDTO = new UserDTO();
        userDTO.setRole(userEntity.getRole());
        userDTO.setEmail(userEntity.getEmail());
        userDTO.setUsername(userEntity.getUsername());
        userDTO.setName(userEntity.getName());

        return userDTO;
    }

    public Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    public String createJwt(UserDTO userDTO) {

        String userId = userDTO.getUsername();
        String role = userDTO.getRole();

        return jwtUtil.createJwt(userId, role, 60 * 60 * 60L);
    }

}
