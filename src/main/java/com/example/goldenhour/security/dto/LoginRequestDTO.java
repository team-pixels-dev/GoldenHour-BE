package com.example.goldenhour.security.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(name="LoginRequestDTO", description = "로그인 요청 DTO")
@Getter
@Setter
public class LoginRequestDTO {

//    @Schema(required = true)
    private String email;

//    @Schema(required = true)
    private String name;

    @Schema(description = "소셜 로그인 공급자명 + OAuth2 서버에서 발급해주는 ID값 (ex. google 123456789)", example = "google 123456789")
    private String userId;

    @Schema(description = "OAuth2 서버 접근을 위한 액세스 토큰")
    private String accessToken;
}
