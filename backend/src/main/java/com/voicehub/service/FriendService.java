package com.voicehub.service;

import com.voicehub.model.*;
import com.voicehub.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FriendService {
    private final UserProfileRepository profiles;
    private final FriendshipRepository friendships;
    private final DirectMessageRepository messages;

    public FriendService(UserProfileRepository profiles, FriendshipRepository friendships, DirectMessageRepository messages) {
        this.profiles = profiles; this.friendships = friendships; this.messages = messages;
    }

    public UserProfile syncProfile(UserProfile incoming) {
        UserProfile profile = profiles.findByFirebaseUid(incoming.getFirebaseUid()).orElseGet(UserProfile::new);
        profile.setFirebaseUid(incoming.getFirebaseUid());
        profile.setEmail(incoming.getEmail().trim().toLowerCase());
        profile.setDisplayName(incoming.getDisplayName().trim());
        profile.setAvatarUrl(incoming.getAvatarUrl());
        profile.setUpdatedAt(LocalDateTime.now());
        return profiles.save(profile);
    }

    public Map<String, Object> request(String requesterUid, String friendEmail) {
        UserProfile target = profiles.findByEmailIgnoreCase(friendEmail.trim())
            .orElseThrow(() -> new IllegalArgumentException("Chưa tìm thấy tài khoản này. Người đó cần đăng nhập SquadCast ít nhất một lần."));
        if (target.getFirebaseUid().equals(requesterUid)) throw new IllegalArgumentException("Bạn không thể kết bạn với chính mình.");
        String low = requesterUid.compareTo(target.getFirebaseUid()) < 0 ? requesterUid : target.getFirebaseUid();
        String high = requesterUid.compareTo(target.getFirebaseUid()) < 0 ? target.getFirebaseUid() : requesterUid;
        Friendship relationship = friendships.findByUserLowUidAndUserHighUid(low, high).orElseGet(Friendship::new);
        if (relationship.getId() != null) {
            return Map.of("status", relationship.getStatus().name(), "message", relationship.getStatus() == Friendship.Status.ACCEPTED ? "Hai bạn đã là bạn bè." : "Lời mời đã được gửi.");
        }
        relationship.setUserLowUid(low); relationship.setUserHighUid(high);
        relationship.setRequesterUid(requesterUid); relationship.setStatus(Friendship.Status.PENDING);
        relationship.setCreatedAt(LocalDateTime.now()); friendships.save(relationship);
        return Map.of("status", "PENDING", "message", "Đã gửi lời mời kết bạn.");
    }

    public void accept(Long id, String uid) {
        Friendship f = friendships.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lời mời."));
        if (f.getStatus() != Friendship.Status.PENDING || f.getRequesterUid().equals(uid) ||
            (!f.getUserLowUid().equals(uid) && !f.getUserHighUid().equals(uid))) throw new IllegalArgumentException("Bạn không thể chấp nhận lời mời này.");
        f.setStatus(Friendship.Status.ACCEPTED); friendships.save(f);
    }

    public Map<String, Object> list(String uid) {
        List<Map<String, Object>> friends = new ArrayList<>();
        for (Friendship f : friendships.findByStatusAndUserLowUidOrStatusAndUserHighUid(Friendship.Status.ACCEPTED, uid, Friendship.Status.ACCEPTED, uid)) {
            String otherUid = f.getUserLowUid().equals(uid) ? f.getUserHighUid() : f.getUserLowUid();
            profiles.findByFirebaseUid(otherUid).ifPresent(p -> friends.add(profileMap(p, f.getId())));
        }
        List<Map<String, Object>> requests = new ArrayList<>();
        for (Friendship f : friendships.findByStatusAndUserHighUid(Friendship.Status.PENDING, uid)) {
            if (f.getRequesterUid().equals(uid)) continue;
            profiles.findByFirebaseUid(f.getRequesterUid()).ifPresent(p -> requests.add(profileMap(p, f.getId())));
        }
        return Map.of("friends", friends, "requests", requests);
    }

    public boolean areFriends(String uid, String otherUid) {
        String low = uid.compareTo(otherUid) < 0 ? uid : otherUid;
        String high = uid.compareTo(otherUid) < 0 ? otherUid : uid;
        return friendships.findByUserLowUidAndUserHighUid(low, high).map(f -> f.getStatus() == Friendship.Status.ACCEPTED).orElse(false);
    }

    public List<DirectMessage> history(String uid, String otherUid) {
        if (!areFriends(uid, otherUid)) throw new IllegalArgumentException("Chỉ bạn bè mới có thể nhắn tin riêng.");
        return messages.findTop100ByConversationKeyOrderByCreatedAtAsc(conversationKey(uid, otherUid));
    }

    public DirectMessage saveMessage(String uid, String otherUid, String content) {
        if (!areFriends(uid, otherUid)) throw new IllegalArgumentException("Chỉ bạn bè mới có thể nhắn tin riêng.");
        if (content == null || content.trim().isEmpty()) throw new IllegalArgumentException("Tin nhắn không được để trống.");
        DirectMessage message = new DirectMessage();
        message.setConversationKey(conversationKey(uid, otherUid)); message.setSenderUid(uid);
        message.setContent(content.trim()); message.setCreatedAt(LocalDateTime.now());
        return messages.save(message);
    }

    public static String conversationKey(String uid, String otherUid) {
        return uid.compareTo(otherUid) < 0 ? uid + "--" + otherUid : otherUid + "--" + uid;
    }

    private Map<String, Object> profileMap(UserProfile p, Long friendshipId) {
        return Map.of("friendshipId", friendshipId, "uid", p.getFirebaseUid(), "email", p.getEmail(), "displayName", p.getDisplayName(), "avatarUrl", Optional.ofNullable(p.getAvatarUrl()).orElse(""));
    }
}
