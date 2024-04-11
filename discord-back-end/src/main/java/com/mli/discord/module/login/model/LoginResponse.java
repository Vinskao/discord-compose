package com.mli.discord.module.login.model;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @Author D3031104
 * @version 1.0
 */
@Schema(description = "登錄響應")
public class LoginResponse {
    private String accessToken;
    private String refreshToken;

    public LoginResponse() {
    }

    public LoginResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LoginResponse accessToken(String accessToken) {
        setAccessToken(accessToken);
        return this;
    }

    public LoginResponse refreshToken(String refreshToken) {
        setRefreshToken(refreshToken);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof LoginResponse)) {
            return false;
        }
        LoginResponse loginResponse = (LoginResponse) o;
        return Objects.equals(accessToken, loginResponse.accessToken)
                && Objects.equals(refreshToken, loginResponse.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, refreshToken);
    }

    @Override
    public String toString() {
        return "{" +
                " accessToken='" + getAccessToken() + "'" +
                ", refreshToken='" + getRefreshToken() + "'" +
                "}";
    }

}
