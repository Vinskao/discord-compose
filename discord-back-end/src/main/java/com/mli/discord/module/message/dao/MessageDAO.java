package com.mli.discord.module.message.dao;

import java.util.List;

import com.mli.discord.module.message.model.Message;

import io.swagger.v3.oas.annotations.Operation;

/**
 * Message DAO MyBatis Mapper
 * 
 * @Author D3031104
 * @Version 1.0
 *          訊息 DAO MyBatis 映射器
 */
public interface MessageDAO {

	/**
	 * 插入訊息
	 * 
	 * @param message 訊息對象
	 */
	@Operation(summary = "插入訊息")
	void insertMessage(Message message);

	/**
	 * 根據房間ID查詢訊息列表
	 * 
	 * @param roomId 房間ID
	 * @return List<Message> 訊息列表
	 */
	@Operation(summary = "根據房間ID查詢訊息列表")
	List<Message> findMessagesByRoomId(Integer roomId);

}
