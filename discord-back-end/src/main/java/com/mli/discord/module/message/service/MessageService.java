package com.mli.discord.module.message.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mli.discord.module.message.dao.MessageDAO;
import com.mli.discord.module.message.dto.MessageDTO;
import com.mli.discord.module.message.model.Message;

import io.swagger.v3.oas.annotations.Operation;

/**
 * 
 * @Author D3031104
 * @version 1.0
 */
@Service
public class MessageService {
    @Autowired
    private MessageDAO messageDAO;

    /**
     * 保存訊息
     * 
     * @param messageDTO 待保存的訊息DTO
     * @return Message 已保存的訊息
     */
    @Operation(summary = "保存訊息")
    public Message saveMessage(MessageDTO messageDTO) {
        Message message = new Message();
        message.setRoomId(messageDTO.getRoomId());
        message.setUsername(messageDTO.getUsername());
        message.setMessage(messageDTO.getMessage());
        message.setType(messageDTO.getType()); // 假设默认都是文本消息
        message.setTime(LocalDateTime.now());

        messageDAO.insertMessage(message);
        return message;
    }

    /**
     * 根據房間ID獲取消息列表
     * 
     * @param roomId 房間ID
     * @return List<Message> 訊息列表
     */
    @Operation(summary = "根據房間ID獲取消息列表")
    public List<Message> getMessagesByRoomId(Integer roomId) {
        return messageDAO.findMessagesByRoomId(roomId);
    }

}
