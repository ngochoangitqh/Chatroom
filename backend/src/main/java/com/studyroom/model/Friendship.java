package com.studyroom.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "friendships", uniqueConstraints = @UniqueConstraint(columnNames = {"user_low_uid", "user_high_uid"}))
public class Friendship {
    public enum Status { PENDING, ACCEPTED }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_low_uid", nullable = false, length = 128)
    private String userLowUid;
    @Column(name = "user_high_uid", nullable = false, length = 128)
    private String userHighUid;
    @Column(nullable = false, length = 128)
    private String requesterUid;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16)
    private Status status;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public String getUserLowUid() { return userLowUid; }
    public void setUserLowUid(String value) { userLowUid = value; }
    public String getUserHighUid() { return userHighUid; }
    public void setUserHighUid(String value) { userHighUid = value; }
    public String getRequesterUid() { return requesterUid; }
    public void setRequesterUid(String value) { requesterUid = value; }
    public Status getStatus() { return status; }
    public void setStatus(Status value) { status = value; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime value) { createdAt = value; }
}

