package com.studyroom.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "direct_messages")
public class DirectMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 300)
    private String conversationKey;
    @Column(nullable = false, length = 128)
    private String senderUid;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    public Long getId() { return id; }
    public String getConversationKey() { return conversationKey; }
    public void setConversationKey(String value) { conversationKey = value; }
    public String getSenderUid() { return senderUid; }
    public void setSenderUid(String value) { senderUid = value; }
    public String getContent() { return content; }
    public void setContent(String value) { content = value; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime value) { createdAt = value; }
}

