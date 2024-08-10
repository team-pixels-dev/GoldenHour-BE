package com.example.goldenhour.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class GoogleResponse implements OAuth2Response {

    public String id;
    public String name;
    public String email;

    @Override
    public String getProvider() {

        return "google";
    }

    @Override
    public String getProviderId() {

        return id;
    }

    @Override
    public String getEmail() {

        return email;
    }

    @Override
    public String getName() {

        return name;
    }
}
