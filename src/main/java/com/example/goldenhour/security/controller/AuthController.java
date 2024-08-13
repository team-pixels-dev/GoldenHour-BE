package com.example.goldenhour.security.controller;

import com.example.goldenhour.dto.UserDTO;
import com.example.goldenhour.exception.http.Http400ResponseException;
import com.example.goldenhour.security.dto.LoginRequestDTO;
import com.example.goldenhour.security.jwt.JWTUtil;
import com.example.goldenhour.security.repository.RefreshRepository;
import com.example.goldenhour.security.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login/{providerName}")
    public ResponseEntity<?> socialLogin(
            @PathVariable("providerName") String providerName,
            @RequestBody LoginRequestDTO loginRequestDTO,
            HttpServletRequest request,
            HttpServletResponse response) {

        UserDTO userDTO = authService.login(loginRequestDTO, providerName);
        Map<String, String> jwtMap = authService.createJwt(userDTO);

        String access = jwtMap.get("access");
        String refresh = jwtMap.get("refresh");

        response.setHeader("access", access);
        response.addCookie(authService.createCookie("refresh", refresh));

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        Map<String, String> newTokenMap = authService.reissueAccessToken(cookies);

        String newAccess = newTokenMap.get("access");
        String newRefresh = newTokenMap.get("refresh");

        response.setHeader("access", newAccess);
        response.addCookie(authService.createCookie("refresh", newRefresh));

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new Http400ResponseException("Cookie is null");
        }
        authService.logout(cookies);

        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

}
