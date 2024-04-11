package com.mli.discord.module.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

/**
 * 
 * @Author D3031104
 * @Version 1.0
 *          登錄請求DTO
 */
@Schema(description = "登錄請求DTO")
public class LoginDTO {
    @Schema(description = "The User's username")
    private String username;

    @Schema(description = "The User's password")
    private String password;

    public LoginDTO() {
    }

    public LoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginDTO username(String username) {
        setUsername(username);
        return this;
    }

    public LoginDTO password(String password) {
        setPassword(password);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof LoginDTO)) {
            return false;
        }
        LoginDTO loginDTO = (LoginDTO) o;
        return Objects.equals(username, loginDTO.username) && Objects.equals(password, loginDTO.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

    @Override
    public String toString() {
        return "{" +
                " username='" + getUsername() + "'" +
                ", password='" + getPassword() + "'" +
                "}";
    }

}