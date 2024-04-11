package com.mli.discord.core.Interceptor;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
/**
 * 自定義HTTP會話握手攔截器，用於在WebSocket握手階段攔截HTTP請求。
 * 若存在HTTP會話且會話中有用戶名，則將用戶名添加到WebSocket會話的屬性中。
 * 
 * @Author D3031104
 * @version 1.0
 */
public class CustomHttpSessionHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    /**
     * 在握手之前執行的回調方法。
     * 
     * @param request 服務器請求
     * @param response 服務器響應
     * @param wsHandler WebSocket處理器
     * @param attributes 從HTTP會話到WebSocket會話的屬性映射
     * @return 是否繼續握手流程
     * @throws Exception 處理過程中可能發生的異常
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession httpSession = servletRequest.getServletRequest().getSession(false);
            if (httpSession != null) {
                String username = (String) httpSession.getAttribute("username");
                if (username != null) {
                    attributes.put("username", username);
                }
            }
        }
        return true;
    }
}
