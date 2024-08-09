package com.example.goldenhour.controller;

import com.example.goldenhour.dto.CustomOAuth2User;
import com.example.goldenhour.entity.UserEntity;
import com.example.goldenhour.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/users/my-info")
    public UserEntity myInfo(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        return userRepository.findByUsername(oAuth2User.getUsername());
    }
}
