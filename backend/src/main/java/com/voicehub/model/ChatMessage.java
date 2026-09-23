package com.voicehub.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatMessage {

    public enum MessageType {
        CHAT, JOIN, LEAVE, TYPING, REACT
    }

    private Long id;
    private MessageType type;
    private String content;
    private String sender;
    private String senderUid;
    private String avatarColor;
    private String avatarUrl;
    private String channel;
    private Long channelId;          // DB ID của ChannelEntity
    private Long serverId;           // DB ID của Server
    private String timestamp;

    // Reply
    private Long replyToId;
    private String replyToSender;
    private String replyToContent;

    // Reactions: emoji -> list of usernames
    private Map<String, List<String>> reactions = new HashMap<>();

    // Typing indicator
    private boolean typing;

    public ChatMessage() {}

    public ChatMessage(MessageType type, String content, String sender, String channel) {
        this.type      = type;
        this.content   = content;
        this.sender    = sender;
        this.channel   = channel;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // ─── Getters & Setters ────────────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getSenderUid() { return senderUid; }
    public void setSenderUid(String senderUid) { this.senderUid = senderUid; }
    public String getAvatarColor() { return avatarColor; }
    public void setAvatarColor(String avatarColor) { this.avatarColor = avatarColor; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }
    public Long getServerId() { return serverId; }
    public void setServerId(Long serverId) { this.serverId = serverId; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public Long getReplyToId() { return replyToId; }
    public void setReplyToId(Long replyToId) { this.replyToId = replyToId; }
    public String getReplyToSender() { return replyToSender; }
    public void setReplyToSender(String replyToSender) { this.replyToSender = replyToSender; }
    public String getReplyToContent() { return replyToContent; }
    public void setReplyToContent(String replyToContent) { this.replyToContent = replyToContent; }
    public Map<String, List<String>> getReactions() { return reactions; }
    public void setReactions(Map<String, List<String>> reactions) { this.reactions = reactions; }
    public boolean isTyping() { return typing; }
    public void setTyping(boolean typing) { this.typing = typing; }
}
