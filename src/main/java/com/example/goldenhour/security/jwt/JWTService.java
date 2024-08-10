package com.example.goldenhour.security.jwt;

import com.example.goldenhour.dto.OAuth2Response;
import com.example.goldenhour.dto.UserDTO;
import com.example.goldenhour.entity.UserEntity;
import com.example.goldenhour.repository.UserRepository;
import com.example.goldenhour.security.oauth2.OAuth2UserInfoProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JWTService {

    private final UserRepository userRepository;

    protected boolean checkJWTUser(UserDTO userDTO) {

        UserEntity existUserEntity = userRepository.findByUsername(userDTO.getUsername());

        if (existUserEntity == null) {
            return false;
        }

        return true;
    }
}
