package com.mli.discord.module.grouping.dto;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 房間 ID DTO。
 * 
 * @author D3031104
 * @version 1.0
 */
@Schema(description = "房間 ID DTO")
public class RoomIdDTO {
    @Schema(description = "房間 ID")
    private Integer roomId;

    public RoomIdDTO() {
    }

    public RoomIdDTO(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getRoomId() {
        return this.roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public RoomIdDTO roomId(Integer roomId) {
        setRoomId(roomId);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof RoomIdDTO)) {
            return false;
        }
        RoomIdDTO roomIdDTO = (RoomIdDTO) o;
        return Objects.equals(roomId, roomIdDTO.roomId);
    }

    @Override
    public String toString() {
        return "{" +
                " roomId='" + getRoomId() + "'" +
                "}";
    }

}
