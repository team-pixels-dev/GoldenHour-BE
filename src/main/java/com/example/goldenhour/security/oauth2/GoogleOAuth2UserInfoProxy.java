package com.example.goldenhour.security.oauth2;

import com.example.goldenhour.dto.GoogleResponse;
import com.example.goldenhour.dto.OAuth2Response;
import com.example.goldenhour.exception.http.Http400ResponseException;
import com.example.goldenhour.exception.http.Http500ResponseException;
import okhttp3.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;

public class GoogleOAuth2UserInfoProxy implements OAuth2UserInfoProxy {

    @Override
    public OAuth2Response getOAuth2UserInfo(String accessToken) {

        String authorization = "Bearer " + accessToken;

        Mono<GoogleResponse> responseMono = WebClient.create("https://www.googleapis.com")
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth2/v2/userinfo")
                        .build(false))
                .header("Authorization", authorization)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new Http400ResponseException("google 서버 연결 실패")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new Http500ResponseException("google 서버 연결 실패")))
                .bodyToMono(GoogleResponse.class);

        return responseMono.block();
    }
}
