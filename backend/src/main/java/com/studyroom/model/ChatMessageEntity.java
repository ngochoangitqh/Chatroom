package com.studyroom.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages", indexes = {
    @Index(name = "idx_channel_created", columnList = "channel, created_at")
})
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatMessage.MessageType type;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 100)
    private String sender;

    @Column(name = "sender_uid", length = 128)
    private String senderUid;

    @Column(name = "avatar_color", length = 20)
    private String avatarColor;

    @Column(name = "avatar_url", length = 512)
    private String avatarUrl;

    @Column(nullable = false, length = 50)
    private String channel;

    @Column(name = "channel_id")
    private Long channelId;         // DB ID của channel (ChannelEntity)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Reply
    @Column(name = "reply_to_id")
    private Long replyToId;

    @Column(name = "reply_to_sender", length = 100)
    private String replyToSender;

    @Column(name = "reply_to_content", columnDefinition = "TEXT")
    private String replyToContent;

    // Reactions lưu dạng JSON string: {"👍": ["user1","user2"], "❤️": ["user3"]}
    @Column(name = "reactions_json", columnDefinition = "TEXT")
    private String reactionsJson;

    public ChatMessageEntity() {}

    // ─── Getters & Setters ────────────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ChatMessage.MessageType getType() { return type; }
    public void setType(ChatMessage.MessageType type) { this.type = type; }

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


    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getReplyToId() { return replyToId; }
    public void setReplyToId(Long replyToId) { this.replyToId = replyToId; }

    public String getReplyToSender() { return replyToSender; }
    public void setReplyToSender(String replyToSender) { this.replyToSender = replyToSender; }

    public String getReplyToContent() { return replyToContent; }
    public void setReplyToContent(String replyToContent) { this.replyToContent = replyToContent; }

    public String getReactionsJson() { return reactionsJson; }
    public void setReactionsJson(String reactionsJson) { this.reactionsJson = reactionsJson; }
}

