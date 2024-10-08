package com.example.goldenhour.controller;

import com.example.goldenhour.dto.CustomOAuth2User;
import com.example.goldenhour.entity.UserEntity;
import com.example.goldenhour.repository.UserRepository;
import com.example.goldenhour.security.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserRepository userRepository;

    @Operation(summary = "유저 정보", description = "로그인한 유저 정보 반환")
    @GetMapping("/users/my-info")
    public UserEntity myInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return userRepository.findByUsername(customUserDetails.getUsername());
    }
}
