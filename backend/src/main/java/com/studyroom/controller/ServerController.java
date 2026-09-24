package com.studyroom.controller;

import com.studyroom.service.ServerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/servers")
@CrossOrigin(origins = "*")
public class ServerController {

    private final ServerService serverService;

    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    // ── POST /api/servers — Tạo server mới ───────────────────────────
    @PostMapping
    public ResponseEntity<?> createServer(@RequestBody Map<String, String> body) {
        try {
            String name      = body.get("name");
            String ownerId   = body.get("ownerId");
            String ownerName = body.get("ownerName");
            if (name == null || name.isBlank() || ownerId == null || ownerId.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu thông tin bắt buộc!"));
            }
            Map<String, Object> result = serverService.createServer(name.trim(), ownerId, ownerName);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ── GET /api/servers/my?uid=... — Lấy servers của user ───────────
    @GetMapping("/my")
    public ResponseEntity<?> getMyServers(@RequestParam String uid) {
        try {
            List<Map<String, Object>> servers = serverService.getMyServers(uid);
            return ResponseEntity.ok(servers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ── POST /api/servers/join — Tham gia qua invite code ───────────
    @PostMapping("/join")
    public ResponseEntity<?> joinServer(@RequestBody Map<String, String> body) {
        try {
            String inviteCode = body.get("inviteCode");
            String uid        = body.get("uid");
            String username   = body.get("username");
            if (inviteCode == null || uid == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu thông tin!"));
            }
            Map<String, Object> result = serverService.joinServer(inviteCode, uid, username);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── GET /api/servers/{id}/channels — Danh sách channels ──────────
    @GetMapping("/{serverId}/channels")
    public ResponseEntity<?> getChannels(@PathVariable Long serverId) {
        try {
            return ResponseEntity.ok(serverService.getChannels(serverId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ── GET /api/servers/{id}/members — Danh sách members ────────────
    @GetMapping("/{serverId}/members")
    public ResponseEntity<?> getMembers(@PathVariable Long serverId) {
        try {
            return ResponseEntity.ok(serverService.getMembers(serverId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ── GET /api/servers/{id}/invite?uid=... — Lấy invite code ───────
    @GetMapping("/{serverId}/invite")
    public ResponseEntity<?> getInviteCode(@PathVariable Long serverId,
                                           @RequestParam String uid) {
        try {
            String code = serverService.getInviteCode(serverId, uid);
            return ResponseEntity.ok(Map.of("inviteCode", code, "serverId", serverId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

