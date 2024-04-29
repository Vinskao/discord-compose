// package com.mli.discord.module.message.websocket;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.stereotype.Component;

// import javax.websocket.*;
// import javax.websocket.server.PathParam;
// import javax.websocket.server.ServerEndpoint;
// import java.io.IOException;
// import java.util.concurrent.ConcurrentHashMap;

// /**
// *
// * @Author D3031104
// * @version 1.0
// */
// @ServerEndpoint("/ws/{id}")
// @Component
// public class WebSocketServer {
// private final static Logger logger =
// LoggerFactory.getLogger(WebSocketServer.class);

// // Using a thread-safe collection to store session and associated user ID
// private static final ConcurrentHashMap<String, Session> sessions = new
// ConcurrentHashMap<>();

// @OnOpen
// public void onOpen(Session session, @PathParam("id") String id) {
// sessions.put(id, session);
// logger.info("WebSocket connection established for id: {}", id);
// }

// @OnMessage
// public void onMessage(String message, @PathParam("id") String id) {
// logger.info("Received message '{}' from client '{}'", message, id);
// // Echo the message back to the sender for confirmation
// sendMessage(id, message);
// }

// @OnClose
// public void onClose(@PathParam("id") String id) {
// sessions.remove(id);
// logger.info("WebSocket connection closed for id: {}", id);
// }

// @OnError
// public void onError(Session session, Throwable error, @PathParam("id") String
// id) {
// logger.error("WebSocket error for id: {}", id, error);
// }

// public void sendMessage(String id, String message) {
// try {
// Session session = sessions.get(id);
// if (session != null) {
// session.getBasicRemote().sendText(message);
// }
// } catch (IOException e) {
// logger.error("Error sending message to client '{}': {}", id, e.getMessage(),
// e);
// }
// }

// // Broadcast a message to all sessions
// public static void broadcast(String message) {
// sessions.values().forEach(session -> {
// try {
// session.getBasicRemote().sendText(message);
// } catch (IOException e) {
// logger.error("Error broadcasting message: {}", e.getMessage(), e);
// }
// });
// }
// }
