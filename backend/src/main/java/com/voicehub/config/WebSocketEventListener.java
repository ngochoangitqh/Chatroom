package com.voicehub.config;

import com.voicehub.model.ChatMessage;
import com.voicehub.service.VoicePresenceService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;
    private final VoicePresenceService voicePresenceService;

    public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate,
                                  VoicePresenceService voicePresenceService) {
        this.messagingTemplate = messagingTemplate;
        this.voicePresenceService = voicePresenceService;
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String channel = (String) headerAccessor.getSessionAttributes().get("channel");

        if (username != null) {
            System.out.println("User da roi: " + username + " (channel: " + channel + ")");
            ChatMessage chatMessage = new ChatMessage(
                ChatMessage.MessageType.LEAVE, null, username, channel != null ? channel : "general"
            );
            messagingTemplate.convertAndSend("/topic/channel/" + (channel != null ? channel : "general"), chatMessage);
        }

        Object voiceChannelId = headerAccessor.getSessionAttributes().get("voiceChannelId");
        String voiceUid = (String) headerAccessor.getSessionAttributes().get("voiceUid");
        if (voiceChannelId instanceof Long channelId && voiceUid != null) {
            messagingTemplate.convertAndSend("/topic/voice/" + channelId,
                voicePresenceService.leave(channelId, voiceUid));
        }
    }
}
