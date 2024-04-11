package com.mli.discord.core.config.web;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.mli.discord.core.Interceptor.CustomHttpSessionHandshakeInterceptor;
import com.mli.discord.module.message.model.Message;
/**
 * WebSocket消息傳遞配置，提供了基於WebSocket的STOMP消息傳遞功能的配置。
 *
 * @Author D3031104
 * @version 1.0
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketBrokerConfig implements WebSocketMessageBrokerConfigurer, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketBrokerConfig.class);

    private Set<String> connectedUsernames = ConcurrentHashMap.newKeySet();
    private ApplicationContext applicationContext;
    // private Map<String, Set<String>> roomUsernamesMap = new
    // ConcurrentHashMap<>();
    
    @MessageMapping("/get-online-users")
    @SendTo("/topic/online-users")
    public Set<String> getOnlineUsers() {
        return connectedUsernames;
    }
    
    /**
     * 註冊STOMP端點。
     *
     * @param registry STOMP端點註冊器
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-message")
                .setAllowedOrigins("http://localhost:8090")
                .withSockJS()
                .setInterceptors(new CustomHttpSessionHandshakeInterceptor());
    }
    /**
     * 配置消息代理。
     *
     * @param config 消息代理註冊器
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * 設置應用程式上下文。
     *
     * @param applicationContext Spring應用程式上下文
     * @throws BeansException 如果設置上下文時發生錯誤
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    /**
     * 處理會話連接事件。
     *
     * @param event 會話連接事件
     */
    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        String username = event.getUser().getName();
        connectedUsernames.add(username);
        logger.info("{} connected", username);
        broadcastUpdatedUserList();
    }

    // @EventListener
    // public void handleSessionConnected(SessionConnectEvent event) {
    // StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
    // String username = sha.getUser().getName();
    // Object roomIdObj = sha.getSessionAttributes().get("roomId");
    //
    // // 检查 roomId 是否存在
    // if (roomIdObj == null) {
    // logger.error("Room ID is null for user: {}", username);
    // return; // 直接返回，不执行后续操作
    // }
    //
    // String roomId = roomIdObj.toString();
    // // 现在 roomId 确认不为 null，可以继续使用
    // roomUsernamesMap.computeIfAbsent(roomId, k ->
    // ConcurrentHashMap.newKeySet()).add(username);
    // logger.info("{} connected to room {}", username, roomId);
    // broadcastUpdatedUserList(roomId);
    // }
    
    /**
     * 處理會話斷開事件。
     *
     * @param event 會話斷開事件
     */
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String username = event.getUser().getName();
        connectedUsernames.remove(username);
        logger.info("{} disconnected", username);
        broadcastUpdatedUserList();
    }
    //
    // @EventListener
    // public void handleSessionDisconnect(SessionDisconnectEvent event) {
    // StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
    // String username = sha.getUser().getName();
    // String roomId = sha.getSessionAttributes().get("roomId").toString(); // 同样，假设
    // roomId 已经存储在会话属性中
    //
    // Set<String> usernamesInRoom = roomUsernamesMap.getOrDefault(roomId,
    // ConcurrentHashMap.newKeySet());
    // usernamesInRoom.remove(username);
    // if (usernamesInRoom.isEmpty()) {
    // roomUsernamesMap.remove(roomId);
    // }
    //
    // logger.info("{} disconnected from room {}", username, roomId);
    //
    // broadcastUpdatedUserList(roomId);
    // }

    /**
     * 廣播更新後的用戶列表。
     */
    private void broadcastUpdatedUserList() {
        SimpMessagingTemplate template = applicationContext.getBean(SimpMessagingTemplate.class);
        if (template != null) {
            // 创建一个新的消息对象，设置为 USER_LIST 类型
            Message userListMessage = new Message();
            userListMessage.setType(Message.ChatType.USER_LIST); // 设置消息类型为 USER_LIST
            userListMessage.setMessage(String.join(",", connectedUsernames)); // 用户名列表转换为字符串

            logger.info("Broadcasting connected usernames: {}", connectedUsernames);
            // 使用模板发送消息，注意这里发送的是 userListMessage 对象
            template.convertAndSend("/topic/message", userListMessage);
        }
    }

    // private void broadcastUpdatedUserList(String roomId) {
    // Set<String> usernamesInRoom = roomUsernamesMap.getOrDefault(roomId,
    // ConcurrentHashMap.newKeySet());
    //
    // SimpMessagingTemplate template =
    // applicationContext.getBean(SimpMessagingTemplate.class);
    // if (template != null) {
    // logger.info("Broadcasting connected usernames in room {}: {}", roomId,
    // usernamesInRoom);
    // template.convertAndSend("/topic/userList/" + roomId, usernamesInRoom);
    // logger.info("Broadcasted in room {}: {}", roomId, usernamesInRoom);
    //
    // }
    // }
    
    /**
     * 在STOMP連接建立時，讀取STOMP幀的headers，並將相關信息保存到會話屬性中。
     *
     * @param registration 頻道註冊器
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public org.springframework.messaging.Message<?> preSend(org.springframework.messaging.Message<?> message,
                    MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String roomId = accessor.getFirstNativeHeader("roomId");
                    if (roomId != null) {
                        accessor.getSessionAttributes().put("roomId", roomId);
                    }
                }
                return message;
            }
        });
    }
    // @Override
    // public void configureClientInboundChannel(ChannelRegistration registration) {
    // registration.interceptors(new ChannelInterceptor() {
    // @Override
    // public Message<?> preSend(Message<?> message, MessageChannel channel) {
    // StompHeaderAccessor accessor =
    // MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    // if (StompCommand.SUBSCRIBE.equals(accessor.getCommand()) ||
    // StompCommand.UNSUBSCRIBE.equals(accessor.getCommand())) {
    // String destination = accessor.getDestination();
    // System.out.println(accessor.getCommand() + " to: " + destination);
    // }
    // return message;
    // }
    // });
    // }
}