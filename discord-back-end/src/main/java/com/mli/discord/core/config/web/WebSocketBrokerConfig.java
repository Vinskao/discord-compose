package com.mli.discord.core.config.web;

import java.security.Principal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.mli.discord.core.interceptor.CustomHttpSessionHandshakeInterceptor;
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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Set<String> connectedUsernames = ConcurrentHashMap.newKeySet();
    private ApplicationContext applicationContext;

    @MessageMapping("/get-online-users")
    @SendTo("/topic/online-users")
    public Set<String> getOnlineUsers() {
        return connectedUsernames;
    }

    /**
     * 註冊STOMP端點。設置了一個 WebSocket 端點，客戶端可以通過這個端點與伺服器建立連接
     *
     * @param registry STOMP端點註冊器
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] originPatterns = { "http://*.localhost", "https://*.localhost", "http://localhost:8091",
                "http://localhost:8090" };
        /// ws-message 是設置給前端連接的 WebSocket 端點。這是 STOMP 通信的接入點
        registry.addEndpoint("/ws-message")
                .setAllowedOriginPatterns(originPatterns)
                // 通過 .withSockJS() 添加 SockJS 支持，這有助於在不支持原生 WebSocket 的瀏覽器中依然能使用類似 WebSocket 的通訊
                .withSockJS()
                // 使用 HttpSessionHandshakeInterceptor 在握手階段可以插入自定義邏輯
                .setInterceptors(new CustomHttpSessionHandshakeInterceptor());

        // 为了确保在登录时username正确设置在会话中并且能够被WebSocket握手拦截器使用，你应该检查以下几个关键点：
        // 登录时会话创建：确保在登录成功后，HTTP会话被创建（如果之前不存在的话）。这一点在你的login方法中通过request.getSession(true)确保了新会话的创建。
        // 会话ID保持一致性：客户端在建立WebSocket连接时使用的会话ID应该和它在登录时使用的HTTP会话ID相同。这通常意味着客户端需要保持cookie的一致性，或者在进行WebSocket连接时能够引用到同一个会话ID。
        // 握手阶段的逻辑：确保HttpSessionHandshakeInterceptor在握手阶段能够从HTTP会话中读取username并正确地放入WebSocket会话属性中。这部分你已经通过自定义拦截器尝试实现。
        // 前端WebSocket客户端：确保客户端在建立WebSocket连接时，正确地传递了需要的HTTP
        // cookies（特别是包含会话ID的cookies），这样才能确保WebSocket服务器端能够关联到正确的HTTP会话。
        // 后端安全配置：确保你的安全配置允许从WebSocket连接传递和使用HTTP cookies。有时候，特别是使用了某些安全框架（如Spring
        // Security）时，可能需要额外配置来允许cookies的传递和识别。
        ;
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
            Message userListMessage = new Message();
            userListMessage.setType(Message.ChatType.USER_LIST);
            userListMessage.setMessage(String.join(",", connectedUsernames));

            logger.info("Broadcasting connected usernames: {}", connectedUsernames);
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

                // 這裡是新增的部分，确保每次訊息傳送前都設定好安全上下文
                ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attr != null) {
                    HttpServletRequest request = attr.getRequest();
                    SecurityContext sc = (SecurityContext) request.getSession()
                            .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
                    SecurityContextHolder.setContext(sc);
                }
                return message;
            }
        });
    }
}