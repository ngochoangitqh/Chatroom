package com.voicehub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "servers")
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "owner_id", nullable = false, length = 128)
    private String ownerId;          // Firebase UID

    @Column(name = "owner_name", length = 100)
    private String ownerName;

    @Column(name = "icon_color", length = 20)
    private String iconColor;

    @Column(name = "invite_code", unique = true, length = 20)
    private String inviteCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Server() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getIconColor() { return iconColor; }
    public void setIconColor(String iconColor) { this.iconColor = iconColor; }
    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
