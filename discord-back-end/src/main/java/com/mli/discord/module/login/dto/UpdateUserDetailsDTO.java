package com.mli.discord.module.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO for updating user details like birthday and interests.
 * 
 * @version 1.0
 * @author D3031104
 */
@Schema(description = "Update User Details")
public class UpdateUserDetailsDTO {

    @Schema(description = "username")
    private String username;
    @Schema(description = "用户生日", example = "1990-01-01T00:00:00")
    private LocalDateTime birthday;

    @Schema(description = "用户兴趣", example = "阅读, 旅游")
    private String interests;

    public UpdateUserDetailsDTO() {
    }

    public UpdateUserDetailsDTO(String username, LocalDateTime birthday, String interests) {
        this.username = username;
        this.birthday = birthday;
        this.interests = interests;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public UpdateUserDetailsDTO username(String username) {
        setUsername(username);
        return this;
    }

    public UpdateUserDetailsDTO birthday(LocalDateTime birthday) {
        setBirthday(birthday);
        return this;
    }

    public UpdateUserDetailsDTO interests(String interests) {
        setInterests(interests);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof UpdateUserDetailsDTO)) {
            return false;
        }
        UpdateUserDetailsDTO updateUserDetailsDTO = (UpdateUserDetailsDTO) o;
        return Objects.equals(username, updateUserDetailsDTO.username)
                && Objects.equals(birthday, updateUserDetailsDTO.birthday)
                && Objects.equals(interests, updateUserDetailsDTO.interests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, birthday, interests);
    }

    @Override
    public String toString() {
        return "{" +
                " username='" + getUsername() + "'" +
                ", birthday='" + getBirthday() + "'" +
                ", interests='" + getInterests() + "'" +
                "}";
    }

}