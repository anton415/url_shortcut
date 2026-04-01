package com.serdyuchenko.urlshortcut.dto;

/**
 * DTO ответа с JWT.
 */
public class TokenResponse {

    private String token;

    public TokenResponse() {
    }

    public TokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
