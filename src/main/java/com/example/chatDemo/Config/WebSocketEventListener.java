package com.example.chatDemo.Config;

import com.example.chatDemo.chat.ChatMessage;
import com.example.chatDemo.chat.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.logging.Logger;

@RequiredArgsConstructor
@Component
public class WebSocketEventListener {
    public static final Logger log = Logger.getLogger(WebSocketEventListener.class.getName());
    private final SimpMessageSendingOperations messageSendingOperations;

    @EventListener
    public void handleSessionDisconnectListener(SessionDisconnectEvent disconnectEvent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(disconnectEvent.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            log.info("User disconnected: " + username);
            var chatMessage = ChatMessage.builder()
                    .type(MessageType.LEAVE)
                    .sender(username)
                    .build();
            messageSendingOperations.convertAndSend("/topic/public", chatMessage);
        }
    }
}
