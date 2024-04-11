package com.mli.discord.core.Interceptor;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.Message;

/**
 * 自定義通道攔截器，用於攔截並處理消息通道中的消息。
 * 當訂閱消息時，將打印訂閱目的地。
 * 
 * @Author D3031104
 * @version 1.0
 */
public class CustomChannelInterceptor implements ChannelInterceptor{
    /**
     * 在消息發送之前對消息進行處理。
     * 
     * @param message 消息體
     * @param channel 消息通道
     * @return 處理後的消息
     */
	@Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            System.out.println("Subscription to: " + destination);
        }
        return message;
    }
}
