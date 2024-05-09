package com.mli.discord.core.interceptor;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * 自定義HTTP會話握手攔截器，用於在WebSocket握手階段攔截HTTP請求。
 * 若存在HTTP會話且會話中有用戶名，則將用戶名添加到WebSocket會話的屬性中。
 * 現在增加將整個SecurityContext保存到WebSocket會話屬性中。
 * 
 * @Author D3031104
 * @version 1.0
 */
public class CustomHttpSessionHandshakeInterceptor extends
        HttpSessionHandshakeInterceptor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 在握手之前執行的回調方法。
     *
     * @param request    服務器請求
     * @param response   服務器響應
     * @param wsHandler  WebSocket處理器
     * @param attributes 從HTTP會話到WebSocket會話的屬性映射
     * @return 是否繼續握手流程
     * @throws Exception 處理過程中可能發生的異常
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        super.beforeHandshake(request, response, wsHandler, attributes);
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession httpSession = servletRequest.getServletRequest().getSession(false);
            if (httpSession != null) {
                logger.debug("HTTP Session found with id: {}", httpSession.getId());
                SecurityContext sc = (SecurityContext) httpSession
                        .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
                if (sc != null) {
                    logger.debug("SecurityContext found in HTTP Session");
                    attributes.put("SPRING_SECURITY_CONTEXT", sc);
                } else {
                    logger.warn("No SecurityContext found in HTTP Session");
                }
            } else {
                logger.warn("No HTTP Session found for request");
            }
        }
        return true;
    }
}
