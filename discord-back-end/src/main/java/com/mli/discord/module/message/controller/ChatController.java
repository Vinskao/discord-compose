package com.mli.discord.module.message.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mli.discord.module.login.dto.UsernameDTO;
import com.mli.discord.module.message.dto.MessageDTO;
import com.mli.discord.module.message.dto.RoomIdDTO;
import com.mli.discord.module.message.dto.RoomUserFileDTO;
import com.mli.discord.module.message.dto.UserFileDTO;
import com.mli.discord.module.message.model.Key;
import com.mli.discord.module.message.model.Message;
import com.mli.discord.module.message.model.RSAEntity;
import com.mli.discord.module.message.service.ChatService;
import com.mli.discord.module.message.service.MessageService;
import com.mli.discord.module.message.service.RSAService;

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
	private RSAService rsaService;
	@Autowired
	private ChatService chatService;
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
		logger.debug("Received message: {}", textMessageDTO.getMessage());

		// Broadcast message
		template.convertAndSend("/topic/message/" + textMessageDTO.getRoomId(), textMessageDTO);

		// Log after broadcasting
		logger.debug("(send) Message broadcasted to /topic/message");

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
	public void receiveAndBroadcastMessage(@Payload MessageDTO messageDTO, SimpMessageHeaderAccessor headerAccessor) {
		logger.info("receiveAndBroadcastMessage username: {}", messageDTO.getUsername());

		// 從 headerAccessor 獲取 session ID
		String sessionId = headerAccessor.getSessionId();
		logger.info("Session ID: {}", sessionId);

		// 從 STOMP 消息獲取 username
		String usernameFromMessage = messageDTO.getUsername();

		// 嘗試從 HttpSession 獲取 username
		String usernameFromSession = (String) headerAccessor.getSessionAttributes().get("username");

		// 最終使用的 username
		String finalUsername = usernameFromMessage != null ? usernameFromMessage : usernameFromSession;

		if (finalUsername == null || finalUsername.isEmpty()) {
			logger.info("Username is missing in both STOMP message and session");
			// 備案：從 /me API 獲取用戶信息
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && authentication.isAuthenticated()) {
				UserDetails userDetails = (UserDetails) authentication.getPrincipal();
				finalUsername = userDetails.getUsername();
				logger.info("Retrieved username from SecurityContextHolder: {}", finalUsername);
			}
		}

		if (finalUsername == null) {
			logger.info("Failed to retrieve username by any means");
			return;
		}

		logger.info("Received STOMP message from {}: {}", finalUsername, messageDTO.getMessage());

		// 儲存和廣播消息
		Message message = messageService.saveMessage(messageDTO);
		template.convertAndSend("/topic/message/" + messageDTO.getRoomId(), message);

		logger.info("(message) STOMP Message broadcasted to /topic/message/" + messageDTO.getRoomId());
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
	 * Endpoint to export chat history as an Excel file, which prompts download with
	 * the specified file name.
	 *
	 * @param userFileDTO DTO containing room ID, file name, and username.
	 * @return ResponseEntity with the Excel file as a byte array.
	 */
	@PostMapping("/export-chat-history")
	public ResponseEntity<?> exportChatHistory(@RequestBody UserFileDTO userFileDTO) {
		try {
			byte[] excelFile = chatService.generateChatHistoryExcel(userFileDTO);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentDisposition(ContentDisposition.builder("attachment")
					.filename(userFileDTO.getFileName())
					.build());
			headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

			return ResponseEntity.ok().headers(headers).body(excelFile);
		} catch (SecurityException se) {
			logger.error("Security exception: " + se.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("驗證數位簽章失敗: " + se.getMessage());
		} catch (IOException e) {
			logger.error("IO exception: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Server error occurred while exporting file.");
		} catch (Exception e) {
			logger.error("General exception: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
		}
	}

	@Operation(summary = "導出並保存聊天歷史記錄")
	@PostMapping("/save-chat-history")
	public ResponseEntity<String> saveChatHistory(@RequestBody RoomUserFileDTO roomUserFileDTO) {
		try {
			String excelBase64 = chatService.generateChatHistoryExcelBase64(roomUserFileDTO);

			// 生成鑰匙對
			Key key = rsaService.createKeyPair();

			// 使用生成的私鑰進行簽名
			String signature = rsaService.signData(excelBase64, key.getPrivateKey());

			// 创建签名记录实体并保存到数据库
			RSAEntity rsaEntity = new RSAEntity();
			rsaEntity.setUsername(roomUserFileDTO.getUsername());
			rsaEntity.setName(roomUserFileDTO.getFileName());
			rsaEntity.setPub(key.getPublicKey());
			rsaEntity.setSignature(signature);
			rsaEntity.setData(excelBase64);
			rsaService.insertSignatureRecord(rsaEntity);

			return ResponseEntity.ok("Chat history exported and saved successfully.");
		} catch (Exception e) {
			logger.error("Error while exporting and saving chat history", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save chat history.");
		}
	}

	/**
	 * Retrieves all RSA entities for a specified username.
	 *
	 * @param username The username to search for RSA entities.
	 * @return A ResponseEntity containing the list of RSA entities.
	 */
	@Operation(summary = "Retrieve all RSA entities for a specified username")
	@PostMapping("/get-rsa-entities")
	public ResponseEntity<List<RSAEntity>> getRSAEntitiesByUsername(@RequestBody UsernameDTO usernameDTO) {
		try {
			RSAEntity rsaEntity = new RSAEntity();
			rsaEntity.setUsername(usernameDTO.getUsername());
			List<RSAEntity> entities = chatService.selectAllFilesByUsername(rsaEntity);
			return ResponseEntity.ok(entities);
		} catch (Exception e) {
			logger.error("Error retrieving RSA entities", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
