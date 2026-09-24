package com.studyroom.controller;

import com.studyroom.model.ChatMessage;
import com.studyroom.model.ChatMessageEntity;
import com.studyroom.service.ChatService;
import com.studyroom.service.VoicePresenceService;
import com.studyroom.model.VoiceParticipant;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;
    private final VoicePresenceService voicePresenceService;

    public ChatController(SimpMessageSendingOperations messagingTemplate, ChatService chatService,
                          VoicePresenceService voicePresenceService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.voicePresenceService = voicePresenceService;
    }

    // ─── Redirect trang chủ ─────────────────────────────────────────────
    @GetMapping("/")
    public String homePage() {
        return "redirect:/login.html";
    }

    // ─── REST: Lấy lịch sử theo channelId (DB ID) ─────────────────────
    @GetMapping("/api/messages/channel/{channelId}")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getMessagesByChannelId(@PathVariable Long channelId) {
        return ResponseEntity.ok(chatService.getRecentMessages(channelId, null));
    }

    @GetMapping("/api/voice/{channelId}/participants")
    @ResponseBody
    public ResponseEntity<List<VoiceParticipant>> getVoiceParticipants(@PathVariable Long channelId) {
        return ResponseEntity.ok(voicePresenceService.getParticipants(channelId));
    }

    // ─── WebSocket: live presence for voice rooms ──────────────────────
    @MessageMapping("/voice.join/{channelId}")
    public void joinVoice(@DestinationVariable Long channelId, @Payload VoiceParticipant participant,
                          SimpMessageHeaderAccessor headerAccessor) {
        if (participant.getUid() == null || participant.getUid().isBlank()) return;
        headerAccessor.getSessionAttributes().put("voiceChannelId", channelId);
        headerAccessor.getSessionAttributes().put("voiceUid", participant.getUid());
        messagingTemplate.convertAndSend("/topic/voice/" + channelId,
            voicePresenceService.join(channelId, participant));
    }

    @MessageMapping("/voice.leave/{channelId}")
    public void leaveVoice(@DestinationVariable Long channelId, @Payload Map<String, String> payload) {
        String uid = payload.get("uid");
        if (uid == null || uid.isBlank()) return;
        messagingTemplate.convertAndSend("/topic/voice/" + channelId,
            voicePresenceService.leave(channelId, uid));
    }

    // WebRTC only needs a lightweight signaling relay. The media stream itself
    // travels directly between browsers, never through this Java server.
    @MessageMapping("/voice.signal/{channelId}")
    public void voiceSignal(@DestinationVariable Long channelId, @Payload Map<String, Object> signal) {
        messagingTemplate.convertAndSend("/topic/voice-signal/" + channelId, signal);
    }

    // Fallback for screen sharing: compressed image frames over the existing
    // WebSocket. It keeps screen sharing usable on networks that block WebRTC.
    @MessageMapping("/voice.frame/{channelId}")
    public void voiceFrame(@DestinationVariable Long channelId, @Payload Map<String, String> frame) {
        if (frame.get("fromUid") == null || frame.get("image") == null) return;
        messagingTemplate.convertAndSend("/topic/voice-frame/" + channelId, frame);
    }

    // ─── REST: Backward compat — lấy lịch sử theo tên channel ─────────
    @GetMapping("/api/messages/{channel}")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getMessagesByChannel(@PathVariable String channel) {
        return ResponseEntity.ok(chatService.getRecentMessages(null, channel));
    }

    // ─── WebSocket: Gửi tin nhắn vào channel (theo channelId) ──────────
    @MessageMapping("/chat.sendMessage/channel/{channelId}")
    public void sendMessageById(@DestinationVariable Long channelId,
                                @Payload ChatMessage chatMessage) {
        chatMessage.setChannelId(channelId);
        chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        if (chatMessage.getAvatarColor() == null || chatMessage.getAvatarColor().isEmpty()) {
            chatMessage.setAvatarColor(chatService.getAvatarColor(chatMessage.getSender()));
        }

        ChatMessageEntity saved = chatService.save(chatMessage);
        chatMessage.setId(saved.getId());

        messagingTemplate.convertAndSend("/topic/channel/" + channelId, chatMessage);
    }

    // ─── WebSocket: Gửi tin nhắn vào channel (tên — backward compat) ──
    @MessageMapping("/chat.sendMessage/{channel}")
    public void sendMessage(@DestinationVariable String channel,
                            @Payload ChatMessage chatMessage) {
        chatMessage.setChannel(channel);
        chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        if (chatMessage.getAvatarColor() == null || chatMessage.getAvatarColor().isEmpty()) {
            chatMessage.setAvatarColor(chatService.getAvatarColor(chatMessage.getSender()));
        }

        ChatMessageEntity saved = chatService.save(chatMessage);
        chatMessage.setId(saved.getId());

        messagingTemplate.convertAndSend("/topic/channel/" + channel, chatMessage);
    }

    // ─── WebSocket: User tham gia channel ───────────────────────────────
    @MessageMapping("/chat.addUser/channel/{channelId}")
    public void addUserById(@DestinationVariable Long channelId,
                            @Payload ChatMessage chatMessage,
                            SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("channelId", channelId.toString());

        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setChannelId(channelId);
        chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        messagingTemplate.convertAndSend("/topic/channel/" + channelId, chatMessage);
    }

    // ─── WebSocket: Backward compat addUser ─────────────────────────────
    @MessageMapping("/chat.addUser/{channel}")
    public void addUser(@DestinationVariable String channel,
                        @Payload ChatMessage chatMessage,
                        SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("channel", channel);

        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setChannel(channel);
        chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        messagingTemplate.convertAndSend("/topic/channel/" + channel, chatMessage);
    }

    // ─── WebSocket: Typing indicator ────────────────────────────────────
    @MessageMapping("/chat.typing/channel/{channelId}")
    public void typingById(@DestinationVariable Long channelId,
                           @Payload ChatMessage chatMessage) {
        chatMessage.setChannelId(channelId);
        chatMessage.setType(ChatMessage.MessageType.TYPING);
        messagingTemplate.convertAndSend("/topic/typing/" + channelId, chatMessage);
    }

    @MessageMapping("/chat.typing/{channel}")
    public void typing(@DestinationVariable String channel,
                       @Payload ChatMessage chatMessage) {
        chatMessage.setChannel(channel);
        chatMessage.setType(ChatMessage.MessageType.TYPING);
        messagingTemplate.convertAndSend("/topic/typing/" + channel, chatMessage);
    }

    // ─── WebSocket: React emoji ──────────────────────────────────────────
    @MessageMapping("/chat.react/channel/{channelId}")
    public void reactById(@DestinationVariable Long channelId,
                          @Payload Map<String, Object> payload) {
        Long messageId = Long.valueOf(payload.get("messageId").toString());
        String emoji   = (String) payload.get("emoji");
        String username = (String) payload.get("username");

        Map<String, List<String>> updated = chatService.toggleReaction(messageId, emoji, username);

        Map<String, Object> response = new HashMap<>();
        response.put("type", "REACT");
        response.put("messageId", messageId);
        response.put("reactions", updated);

        messagingTemplate.convertAndSend("/topic/channel/" + channelId, response);
    }

    @MessageMapping("/chat.react/{channel}")
    public void react(@DestinationVariable String channel,
                      @Payload Map<String, Object> payload) {
        Long messageId  = Long.valueOf(payload.get("messageId").toString());
        String emoji    = (String) payload.get("emoji");
        String username = (String) payload.get("username");

        Map<String, List<String>> updated = chatService.toggleReaction(messageId, emoji, username);

        Map<String, Object> response = new HashMap<>();
        response.put("type", "REACT");
        response.put("messageId", messageId);
        response.put("reactions", updated);

        messagingTemplate.convertAndSend("/topic/channel/" + channel, response);
    }
}

