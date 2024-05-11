package com.mli.discord.module.message.dto;

import java.util.Objects;

public class RoomUserFileDTO {
    private Integer roomId;
    private String fileName;
    private String username;

    public RoomUserFileDTO() {
    }

    public RoomUserFileDTO(Integer roomId, String fileName, String username) {
        this.roomId = roomId;
        this.fileName = fileName;
        this.username = username;
    }

    public Integer getRoomId() {
        return this.roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public RoomUserFileDTO roomId(Integer roomId) {
        setRoomId(roomId);
        return this;
    }

    public RoomUserFileDTO fileName(String fileName) {
        setFileName(fileName);
        return this;
    }

    public RoomUserFileDTO username(String username) {
        setUsername(username);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof RoomUserFileDTO)) {
            return false;
        }
        RoomUserFileDTO roomUserFileDTO = (RoomUserFileDTO) o;
        return Objects.equals(roomId, roomUserFileDTO.roomId) && Objects.equals(fileName, roomUserFileDTO.fileName)
                && Objects.equals(username, roomUserFileDTO.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId, fileName, username);
    }

    @Override
    public String toString() {
        return "{" +
                " roomId='" + getRoomId() + "'" +
                ", fileName='" + getFileName() + "'" +
                ", username='" + getUsername() + "'" +
                "}";
    }

}
