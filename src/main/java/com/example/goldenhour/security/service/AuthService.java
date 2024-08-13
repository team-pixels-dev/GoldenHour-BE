package com.example.goldenhour.security.service;

import com.example.goldenhour.dto.OAuth2Response;
import com.example.goldenhour.dto.UserDTO;
import com.example.goldenhour.entity.UserEntity;
import com.example.goldenhour.exception.http.Http400ResponseException;
import com.example.goldenhour.repository.UserRepository;
import com.example.goldenhour.security.dto.LoginRequestDTO;
import com.example.goldenhour.security.entity.RefreshEntity;
import com.example.goldenhour.security.jwt.JWTUtil;
import com.example.goldenhour.security.oauth2.OAuth2UserInfoProxy;
import com.example.goldenhour.security.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final Map<String, OAuth2UserInfoProxy> oAuth2UserInfoProxyMap;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

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

        if (!checkLoginUser(loginRequestDTO, oAuth2Response)) {

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
        userEntity.setUsername(loginRequestDTO.getUserId());
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

    public Map<String, String> createJwt(UserDTO userDTO) {

        String userId = userDTO.getUsername();
        String role = userDTO.getRole();

        String access = jwtUtil.createJwt("access", userId, role, 60 * 60 * 60L);
        String refresh = jwtUtil.createJwt("refresh", userId, role, 86400000L);

        addRefreshEntity(userId, refresh, 86400000L);

        return Map.of("access", access, "refresh", refresh);
    }

    public Map<String, String> reissueAccessToken(Cookie[] cookies) {

        String refresh = getRefreshToken(cookies);

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(username, refresh, 86400000L);

        return Map.of("access", newAccess, "refresh", newRefresh);
    }

    private String getRefreshToken(Cookie[] cookies) {

        String refresh = null;

        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {

            throw new Http400ResponseException("refresh token null");
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            throw new Http400ResponseException("refresh token expired");
        }

        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            throw new Http400ResponseException("invalid refresh token");
        }

        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {

            throw new Http400ResponseException("invalid refresh token");
        }
        return refresh;
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    public void logout(Cookie[] cookies) {

        String refresh = getRefreshToken(cookies);

        refreshRepository.deleteByRefresh(refresh);
    }
}
