package com.mli.discord.module.login.dto;

import java.util.Objects;

public class AuthenticationResponse {
    private String status;
    private String token;

    public AuthenticationResponse(String status, String token) {
        this.status = status;
        this.token = token;
    }

    public static AuthenticationResponseBuilder builder() {
        return new AuthenticationResponseBuilder();
    }

    // Builder class
    public static class AuthenticationResponseBuilder {
        private String status;
        private String token;

        public AuthenticationResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public AuthenticationResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AuthenticationResponse build() {
            return new AuthenticationResponse(status, token);
        }
    }

    // Getters for status and token
    public String getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }

    public AuthenticationResponse() {
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AuthenticationResponse status(String status) {
        setStatus(status);
        return this;
    }

    public AuthenticationResponse token(String token) {
        setToken(token);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof AuthenticationResponse)) {
            return false;
        }
        AuthenticationResponse authenticationResponse = (AuthenticationResponse) o;
        return Objects.equals(status, authenticationResponse.status)
                && Objects.equals(token, authenticationResponse.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, token);
    }

    @Override
    public String toString() {
        return "{" +
                " status='" + getStatus() + "'" +
                ", token='" + getToken() + "'" +
                "}";
    }

}
