package com.example.goldenhour.security.controller;

import com.example.goldenhour.dto.CustomOAuth2User;
import com.example.goldenhour.dto.OAuth2Response;
import com.example.goldenhour.dto.UserDTO;
import com.example.goldenhour.security.dto.LoginRequestDTO;
import com.example.goldenhour.security.jwt.JWTUtil;
import com.example.goldenhour.security.oauth2.OAuth2UserInfoProxy;
import com.example.goldenhour.security.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final JWTUtil jwtUtil;
    private final AuthService authService;

    @GetMapping("/login/{providerName}")
    public ResponseEntity<?> socialLogin(
            @PathVariable("providerName") String providerName,
            @RequestBody LoginRequestDTO loginRequestDTO,
            HttpServletRequest request,
            HttpServletResponse response) {

        UserDTO userDTO = authService.login(loginRequestDTO, providerName);
        String jwtToken = authService.createJwt(userDTO);
        response.addCookie(authService.createCookie("Authorization", jwtToken));

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
