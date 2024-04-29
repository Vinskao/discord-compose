package com.mli.discord.core.config.web;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

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
        String allowedOrigin = System.getenv("CORS_ALLOWED_ORIGIN");
        if (allowedOrigin == null || allowedOrigin.isEmpty()) {
            logger.error("CORS_ALLOWED_ORIGIN is not set! Defaulting to localhost.");
            allowedOrigin = "http://localhost:8090";
        }

        registry.addEndpoint("/ws-message")
                .setAllowedOrigins(allowedOrigin)
                .withSockJS()
                .setInterceptors(new HttpSessionHandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                            attributes.put("username", userDetails.getUsername());
                        }
                        return true;
                    }
                });
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
        Principal principal = event.getUser();
        if (principal != null) {
            String username = principal.getName();
            connectedUsernames.add(username);
            logger.info("{} connected", username);
        } else {
            logger.warn("A connection attempt without authentication");
            return;
        }
        broadcastUpdatedUserList();
    }

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
}