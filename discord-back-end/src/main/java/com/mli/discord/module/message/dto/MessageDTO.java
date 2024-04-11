package com.mli.discord.module.message.dto;

import java.util.Objects;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

import com.mli.discord.module.message.model.Message.ChatType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 
 * @Author D3031104
 * @version 1.0
 */
@Data
@MessageMapping("/message")
@SendTo("/topic/message")
@Schema(description = "WebSocket 訊息的數據傳輸對象")
public class MessageDTO {
    private String message;
    private String username;
    private Integer roomId;
    private ChatType type;

    public MessageDTO() {
    }

    public MessageDTO(String message, String username, Integer roomId, ChatType type) {
        this.message = message;
        this.username = username;
        this.roomId = roomId;
        this.type = type;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getRoomId() {
        return this.roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public ChatType getType() {
        return this.type;
    }

    public void setType(ChatType type) {
        this.type = type;
    }

    public MessageDTO message(String message) {
        setMessage(message);
        return this;
    }

    public MessageDTO username(String username) {
        setUsername(username);
        return this;
    }

    public MessageDTO roomId(Integer roomId) {
        setRoomId(roomId);
        return this;
    }

    public MessageDTO type(ChatType type) {
        setType(type);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MessageDTO)) {
            return false;
        }
        MessageDTO messageDTO = (MessageDTO) o;
        return Objects.equals(message, messageDTO.message) && Objects.equals(username, messageDTO.username)
                && Objects.equals(roomId, messageDTO.roomId) && Objects.equals(type, messageDTO.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, username, roomId, type);
    }

    @Override
    public String toString() {
        return "{" +
                " message='" + getMessage() + "'" +
                ", username='" + getUsername() + "'" +
                ", roomId='" + getRoomId() + "'" +
                ", type='" + getType() + "'" +
                "}";
    }

}
