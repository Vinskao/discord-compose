package com.mli.discord.module.login.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @Author D3031104
 * @version 1.0
 *          註冊請求DTO
 */
@Schema(description = "註冊請求DTO")
public class RegisterDTO {
    private String username;
    private String password;
    private LocalDateTime birthday;
    private String interests;

    public RegisterDTO() {
    }

    public RegisterDTO(String username, String password, LocalDateTime birthday, String interests) {
        this.username = username;
        this.password = password;
        this.birthday = birthday;
        this.interests = interests;
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

    public LocalDateTime getBirthday() {
        return this.birthday;
    }

    public void setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }

    public String getInterests() {
        return this.interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public RegisterDTO username(String username) {
        setUsername(username);
        return this;
    }

    public RegisterDTO password(String password) {
        setPassword(password);
        return this;
    }

    public RegisterDTO birthday(LocalDateTime birthday) {
        setBirthday(birthday);
        return this;
    }

    public RegisterDTO interests(String interests) {
        setInterests(interests);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof RegisterDTO)) {
            return false;
        }
        RegisterDTO registerDTO = (RegisterDTO) o;
        return Objects.equals(username, registerDTO.username) && Objects.equals(password, registerDTO.password)
                && Objects.equals(birthday, registerDTO.birthday) && Objects.equals(interests, registerDTO.interests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, birthday, interests);
    }

    @Override
    public String toString() {
        return "{" +
                " username='" + getUsername() + "'" +
                ", password='" + getPassword() + "'" +
                ", birthday='" + getBirthday() + "'" +
                ", interests='" + getInterests() + "'" +
                "}";
    }

}
