package com.studyroom.model;

/**
 * Ephemeral presence data for a voice channel. This is intentionally not
 * persisted: entering a voice room is a live network state, not chat history.
 */
public class VoiceParticipant {
    private String uid;
    private String displayName;
    private String avatarUrl;
    private String avatarColor;

    public VoiceParticipant() {}

    public VoiceParticipant(String uid, String displayName, String avatarUrl, String avatarColor) {
        this.uid = uid;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.avatarColor = avatarColor;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getAvatarColor() { return avatarColor; }
    public void setAvatarColor(String avatarColor) { this.avatarColor = avatarColor; }
}

