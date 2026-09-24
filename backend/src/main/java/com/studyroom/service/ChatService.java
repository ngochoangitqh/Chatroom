package com.studyroom.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyroom.model.ChatMessage;
import com.studyroom.model.ChatMessageEntity;
import com.studyroom.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ChatService {

    private final ChatMessageRepository repository;
    private final ObjectMapper objectMapper;

    private static final String[] AVATAR_COLORS = {
        "#5865f2","#ed4245","#23a55a","#f0b232","#eb459e",
        "#57f287","#fee75c","#9b59b6","#3498db","#e67e22"
    };

    public ChatService(ChatMessageRepository repository) {
        this.repository  = repository;
        this.objectMapper = new ObjectMapper();
    }

    public String getAvatarColor(String sender) {
        if (sender == null || sender.isEmpty()) return AVATAR_COLORS[0];
        int idx = Math.abs(sender.hashCode()) % AVATAR_COLORS.length;
        return AVATAR_COLORS[idx];
    }

    // ── Lưu tin nhắn (hỗ trợ cả channelId và channelName) ────────────
    public ChatMessageEntity save(ChatMessage msg) {
        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setType(msg.getType());
        entity.setContent(msg.getContent());
        entity.setSender(msg.getSender());
        entity.setSenderUid(msg.getSenderUid());
        entity.setAvatarColor(
            msg.getAvatarColor() != null ? msg.getAvatarColor() : getAvatarColor(msg.getSender())
        );
        entity.setAvatarUrl(msg.getAvatarUrl());
        entity.setChannel(msg.getChannel() != null ? msg.getChannel() : "");
        entity.setChannelId(msg.getChannelId());          // DB channelId
        entity.setCreatedAt(LocalDateTime.now());
        entity.setReplyToId(msg.getReplyToId());
        entity.setReplyToSender(msg.getReplyToSender());
        entity.setReplyToContent(msg.getReplyToContent());
        return repository.save(entity);
    }

    // ── Lấy lịch sử theo channelId (ưu tiên) hoặc channelName ────────
    public List<ChatMessage> getRecentMessages(Long channelId, String channelName) {
        List<ChatMessageEntity> entities;
        if (channelId != null) {
            entities = repository.findTop50ByChannelIdOrderByCreatedAtAsc(channelId);
        } else {
            entities = repository.findTop50ByChannelOrderByCreatedAtAsc(channelName);
        }
        List<ChatMessage> messages = new ArrayList<>();
        for (ChatMessageEntity e : entities) messages.add(toDto(e));
        return messages;
    }

    // ── Backward compat ───────────────────────────────────────────────
    public List<ChatMessage> getRecentMessages(String channel) {
        return getRecentMessages(null, channel);
    }

    // ── DTO ────────────────────────────────────────────────────────────
    public ChatMessage toDto(ChatMessageEntity e) {
        ChatMessage msg = new ChatMessage();
        msg.setId(e.getId());
        msg.setType(e.getType());
        msg.setContent(e.getContent());
        msg.setSender(e.getSender());
        msg.setSenderUid(e.getSenderUid());
        msg.setAvatarColor(
            e.getAvatarColor() != null ? e.getAvatarColor() : getAvatarColor(e.getSender())
        );
        msg.setAvatarUrl(e.getAvatarUrl());
        msg.setChannel(e.getChannel());
        msg.setChannelId(e.getChannelId());
        msg.setTimestamp(
            e.getCreatedAt() != null
                ? e.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm"))
                : ""
        );
        msg.setReplyToId(e.getReplyToId());
        msg.setReplyToSender(e.getReplyToSender());
        msg.setReplyToContent(e.getReplyToContent());

        if (e.getReactionsJson() != null && !e.getReactionsJson().isEmpty()) {
            try {
                msg.setReactions(objectMapper.readValue(e.getReactionsJson(), new TypeReference<>() {}));
            } catch (Exception ex) {
                msg.setReactions(new HashMap<>());
            }
        }
        return msg;
    }

    // ── Toggle reaction ────────────────────────────────────────────────
    public Map<String, List<String>> toggleReaction(Long messageId, String emoji, String username) {
        Optional<ChatMessageEntity> opt = repository.findById(messageId);
        if (opt.isEmpty()) return new HashMap<>();

        ChatMessageEntity entity = opt.get();
        Map<String, List<String>> reactions;
        try {
            reactions = entity.getReactionsJson() != null && !entity.getReactionsJson().isEmpty()
                ? objectMapper.readValue(entity.getReactionsJson(), new TypeReference<>() {})
                : new HashMap<>();
        } catch (Exception ex) { reactions = new HashMap<>(); }

        reactions.computeIfAbsent(emoji, k -> new ArrayList<>());
        List<String> users = reactions.get(emoji);
        if (users.contains(username)) {
            users.remove(username);
            if (users.isEmpty()) reactions.remove(emoji);
        } else {
            users.add(username);
        }

        try {
            entity.setReactionsJson(objectMapper.writeValueAsString(reactions));
            repository.save(entity);
        } catch (Exception ex) { ex.printStackTrace(); }

        return reactions;
    }
}

