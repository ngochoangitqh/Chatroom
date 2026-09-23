package com.voicehub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "server_members", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"server_id", "user_id"})
})
public class ServerMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "server_id", nullable = false)
    private Long serverId;

    @Column(name = "user_id", nullable = false, length = 128)
    private String userId;           // Firebase UID

    @Column(nullable = false, length = 100)
    private String username;

    @Column(length = 20)
    private String role;             // OWNER, MEMBER

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    public ServerMember() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getServerId() { return serverId; }
    public void setServerId(Long serverId) { this.serverId = serverId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
