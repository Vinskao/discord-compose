package com.mli.discord.module.grouping.model;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 
 * 
 * @Author D3031104
 * @version 1.0
 */
@Data
@Schema(description = "用戶與房間關聯實體")
public class UserToRoom {
    private String username;
    private int roomId;

    public UserToRoom() {
    }

    public UserToRoom(String username, int roomId) {
        this.username = username;
        this.roomId = roomId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRoomId() {
        return this.roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public UserToRoom username(String username) {
        setUsername(username);
        return this;
    }

    public UserToRoom roomId(int roomId) {
        setRoomId(roomId);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof UserToRoom)) {
            return false;
        }
        UserToRoom userToRoom = (UserToRoom) o;
        return Objects.equals(username, userToRoom.username) && roomId == userToRoom.roomId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, roomId);
    }

    @Override
    public String toString() {
        return "{" +
                " username='" + getUsername() + "'" +
                ", roomId='" + getRoomId() + "'" +
                "}";
    }

}
