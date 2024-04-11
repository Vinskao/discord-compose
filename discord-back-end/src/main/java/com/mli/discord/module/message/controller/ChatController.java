package com.mli.discord.module.message.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mli.discord.module.message.dto.MessageDTO;
import com.mli.discord.module.message.dto.RoomIdDTO;
import com.mli.discord.module.message.model.Message;
import com.mli.discord.module.message.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @Author D3031104
 * @Version 1.0
 *          接收Client送來的WebSocket訊息及推送給前端的訊息
 */
@RestController
@Tag(name = "Chat Controller", description = "接收Client送來的WebSocket訊息及推送給前端的訊息")
public class ChatController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MessageService messageService;

	@Autowired
	SimpMessagingTemplate template;

	/**
	 * 發送消息給 WebSocket 客戶端
	 * 
	 * @param textMessageDTO 待發送的消息
	 * @return ResponseEntity<Void>
	 */
	@Operation(summary = "發送消息給 WebSocket 客戶端")
	@PostMapping("/send")
	public ResponseEntity<Void> sendMessage(@RequestBody MessageDTO textMessageDTO) {

		// Log received message
		System.out.println("Received message: " + textMessageDTO.getMessage());

		// Broadcast message
		template.convertAndSend("/topic/message/" + textMessageDTO.getRoomId(), textMessageDTO);

		// Log after broadcasting
		System.out.println("(send) Message broadcasted to /topic/message");

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 接收 WebSocket 客戶端發送的消息
	 * 
	 * @param textMessageDTO 接收到的消息
	 */
	@Operation(summary = "接收 WebSocket 客戶端發送的消息")
	@MessageMapping("/sendMessage")
	public void receiveMessage(@Payload MessageDTO messageDTO) {
		logger.info("Received STOMP message: {} from user: {} in room: {}", messageDTO.getMessage(),
				messageDTO.getUsername(), messageDTO.getRoomId());

		template.convertAndSend("/topic/message/" + messageDTO.getRoomId(), messageDTO);

		logger.info("(sendMessage) Broadcasted STOMP message to /topic/message/{}", messageDTO.getRoomId());
	}

	/**
	 * 將消息廣播給 WebSocket 客戶端
	 * 
	 * @param messageDTO 待廣播的消息
	 * @return MessageDTO
	 */
	@Operation(summary = "將消息廣播給 WebSocket 客戶端")
	@SendTo("/topic/message")
	public MessageDTO broadcastMessage(@Payload MessageDTO textMessageDTO) {
		// 日誌記錄，以便於調試和監控
		logger.info("Broadcasting message from {}: {}", textMessageDTO.getUsername(), textMessageDTO.getMessage());
		// 將消息廣播到 /topic/message
		template.convertAndSend("/topic/message/" + textMessageDTO.getRoomId(), textMessageDTO);

		return textMessageDTO;
	}

	/**
	 * 處理從前端 STOMP 客戶端發送到 /app/message 的消息
	 * 
	 * @param messageDTO     接收到的消息
	 * @param headerAccessor 消息頭訪問器
	 */
	@Operation(summary = "處理從前端 STOMP 客戶端發送到 /app/message 的消息")
	@MessageMapping("/message")
	public void receiveAndBroadcastMessage(@Payload MessageDTO textMessageDTO,
			SimpMessageHeaderAccessor headerAccessor) {
		Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
		if (sessionAttributes == null) {
			logger.info("Session attributes are null");
			return;
		}

		if (!sessionAttributes.containsKey("username")) {
			logger.info("Username is not found in WebSocket session attributes.");
			return;
		}

		// 從sessionAttributes中獲取username，並處理它
		String username = (String) sessionAttributes.get("username");
		textMessageDTO.setUsername(username);
		logger.info("Received STOMP message from {}: {}", username, textMessageDTO.getMessage());
		Message message = messageService.saveMessage(textMessageDTO);
		template.convertAndSend("/topic/message/" + textMessageDTO.getRoomId(), message);
		logger.info("(message) STOMP Message broadcasted to /topic/message/" + textMessageDTO.getRoomId());

	}

	/**
	 * 根據房間ID獲取消息
	 * 
	 * @param roomIdDTO 房間IDDTO
	 * @return ResponseEntity<List<Message>> 消息列表
	 */
	@PostMapping("/get-messages")
	@Operation(summary = "根據房間ID獲取消息")
	public ResponseEntity<List<Message>> getMessagesByRoomId(@RequestBody RoomIdDTO roomIdDTO) {
		List<Message> messages = messageService.getMessagesByRoomId(roomIdDTO.getRoomId());
		return ResponseEntity.ok(messages);
	}

	/**
	 * 導出聊天歷史記錄為Excel文件。
	 * 
	 * @param roomIdDTO 房間IDDTO
	 * @return ResponseEntity<byte[]> 包含Excel文件的響應實體
	 */
	@Operation(summary = "導出聊天歷史記錄為Excel文件")
	@PostMapping("/export-chat-history")
	public ResponseEntity<byte[]> exportChatHistory(@RequestBody RoomIdDTO roomIdDTO) {
		logger.info("Exporting chat history for room ID: {}", roomIdDTO.getRoomId());

		// 创建新的Excel工作簿
		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet sheet = workbook.createSheet("Chat History");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

			// 创建标题行
			Row headerRow = sheet.createRow(0);
			String[] headerStrings = { "Type", "Username", "Time", "Message" };
			for (int i = 0; i < headerStrings.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headerStrings[i]);
			}

			// 填充数据
			List<Message> messages = messageService.getMessagesByRoomId(roomIdDTO.getRoomId());
			int rowNum = 1;
			for (Message msg : messages) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(msg.getType().toString());
				row.createCell(1).setCellValue(msg.getUsername());
				row.createCell(2).setCellValue(msg.getTime().format(formatter));
				row.createCell(3).setCellValue(msg.getMessage());
			}

			// 将工作簿写入到字节输出流
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);

			// 设置响应头信息
			HttpHeaders headers = new HttpHeaders();
			headers.setContentDisposition(
					ContentDisposition.builder("attachment").filename("chat_history.xlsx").build());
			headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

			return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
		} catch (IOException e) {
			logger.error("Error while exporting chat history", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
