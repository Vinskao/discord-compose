package com.mli.discord.module.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

/**
 * 代表用戶名稱的數據傳輸對象 (DTO)。
 * 
 * @version 1.0
 * @author D3031104
 */
@Schema(description = "用戶名稱 DTO")
public class UsernameDTO {
    @Schema(description = "用戶名稱", example = "user123")
    private String username;

    public UsernameDTO() {
    }

    public UsernameDTO(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UsernameDTO username(String username) {
        setUsername(username);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof UsernameDTO)) {
            return false;
        }
        UsernameDTO usernameDTO = (UsernameDTO) o;
        return Objects.equals(username, usernameDTO.username);
    }

    @Override
    public String toString() {
        return "{" +
                " username='" + getUsername() + "'" +
                "}";
    }

}
