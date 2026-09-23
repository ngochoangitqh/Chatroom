package com.voicehub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "firebase_uid", nullable = false, unique = true, length = 128)
    private String firebaseUid;
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    @Column(nullable = false, length = 100)
    private String displayName;
    @Column(length = 1000)
    private String avatarUrl;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public String getFirebaseUid() { return firebaseUid; }
    public void setFirebaseUid(String firebaseUid) { this.firebaseUid = firebaseUid; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
