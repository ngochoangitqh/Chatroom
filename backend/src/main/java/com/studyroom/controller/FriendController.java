package com.studyroom.controller;

import com.studyroom.model.DirectMessage;
import com.studyroom.model.UserProfile;
import com.studyroom.service.FriendService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class FriendController {
    private final FriendService friends;
    private final SimpMessageSendingOperations messaging;
    public FriendController(FriendService friends, SimpMessageSendingOperations messaging) { this.friends = friends; this.messaging = messaging; }

    @PostMapping("/api/profiles/sync")
    public ResponseEntity<?> sync(@RequestBody UserProfile profile) {
        if (profile.getFirebaseUid() == null || profile.getEmail() == null || profile.getDisplayName() == null) return bad("Thiếu thông tin tài khoản.");
        return ResponseEntity.ok(friends.syncProfile(profile));
    }
    @GetMapping("/api/friends") public ResponseEntity<?> list(@RequestParam String uid) { return ResponseEntity.ok(friends.list(uid)); }
    @PostMapping("/api/friends/request") public ResponseEntity<?> request(@RequestBody Map<String, String> body) {
        try { return ResponseEntity.ok(friends.request(body.get("uid"), body.get("email"))); } catch (IllegalArgumentException e) { return bad(e.getMessage()); }
    }
    @PostMapping("/api/friends/{id}/accept") public ResponseEntity<?> accept(@PathVariable Long id, @RequestParam String uid) {
        try { friends.accept(id, uid); return ResponseEntity.ok(Map.of("message", "Đã trở thành bạn bè.")); } catch (IllegalArgumentException e) { return bad(e.getMessage()); }
    }
    @GetMapping("/api/dms") public ResponseEntity<?> history(@RequestParam String uid, @RequestParam String friendUid) {
        try { return ResponseEntity.ok(friends.history(uid, friendUid)); } catch (IllegalArgumentException e) { return bad(e.getMessage()); }
    }
    @MessageMapping("/dm.send/{friendUid}")
    public void send(@DestinationVariable String friendUid, @Payload Map<String, String> body) {
        String uid = body.get("uid");
        try { DirectMessage saved = friends.saveMessage(uid, friendUid, body.get("content")); messaging.convertAndSend("/topic/dm/" + FriendService.conversationKey(uid, friendUid), saved); } catch (IllegalArgumentException ignored) { }
    }
    @MessageMapping("/call.signal/{conversationKey}")
    public void callSignal(@DestinationVariable String conversationKey, @Payload Map<String, Object> signal) {
        messaging.convertAndSend("/topic/call/" + conversationKey, signal);
    }
    private ResponseEntity<Map<String, String>> bad(String message) { return ResponseEntity.badRequest().body(Map.of("error", message)); }
}

