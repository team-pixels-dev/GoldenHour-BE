package com.example.goldenhour.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    private String email;
    private String name;
    private String userId;
    private String accessToken;
}
