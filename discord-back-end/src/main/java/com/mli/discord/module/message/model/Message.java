package com.mli.discord.module.message.model;

import java.time.LocalDateTime;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 傳輸的訊息
 * 
 * 
 * @Author D3031104
 * @version 1.0
 */
@Schema(description = "傳輸的訊息")
@Data
public class Message {
    private Integer id;
    private Integer roomId;
    private String username;
    private String message;

    /** 訊息種類 */
    private ChatType type;
    private LocalDateTime time;

    /**
     * 訊息種類Enum
     */
    public enum ChatType {
        TEXT,
        JOIN,
        LEAVE,
        USER_LIST
    }

    public Message() {
    }

    public Message(Integer id, Integer roomId, String username, String message, ChatType type, LocalDateTime time) {
        this.id = id;
        this.roomId = roomId;
        this.username = username;
        this.message = message;
        this.type = type;
        this.time = time;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoomId() {
        return this.roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChatType getType() {
        return this.type;
    }

    public void setType(ChatType type) {
        this.type = type;
    }

    public LocalDateTime getTime() {
        return this.time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Message id(Integer id) {
        setId(id);
        return this;
    }

    public Message roomId(Integer roomId) {
        setRoomId(roomId);
        return this;
    }

    public Message username(String username) {
        setUsername(username);
        return this;
    }

    public Message message(String message) {
        setMessage(message);
        return this;
    }

    public Message type(ChatType type) {
        setType(type);
        return this;
    }

    public Message time(LocalDateTime time) {
        setTime(time);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Message)) {
            return false;
        }
        Message message = (Message) o;
        return Objects.equals(id, message.id) && Objects.equals(roomId, message.roomId)
                && Objects.equals(username, message.username) && Objects.equals(message, message.message)
                && Objects.equals(type, message.type) && Objects.equals(time, message.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roomId, username, message, type, time);
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", roomId='" + getRoomId() + "'" +
                ", username='" + getUsername() + "'" +
                ", message='" + getMessage() + "'" +
                ", type='" + getType() + "'" +
                ", time='" + getTime() + "'" +
                "}";
    }

}