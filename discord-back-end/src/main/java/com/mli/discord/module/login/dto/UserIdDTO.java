package com.mli.discord.module.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 *
 * @version 1.0
 * @author D3031104
 */
@Schema(description = "用戶id")
public class UserIdDTO {
    /** 使用者ID */
    @Schema(description = "用戶ID", name = "id")
    private int id;

    public UserIdDTO() {
    }

    public UserIdDTO(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserIdDTO id(int id) {
        setId(id);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof UserIdDTO)) {
            return false;
        }
        UserIdDTO userIdDTO = (UserIdDTO) o;
        return id == userIdDTO.id;
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                "}";
    }

}
