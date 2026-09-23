package com.voicehub.service;

import com.voicehub.model.*;
import com.voicehub.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ServerService {

    private final ServerRepository serverRepository;
    private final ServerMemberRepository memberRepository;
    private final ChannelRepository channelRepository;

    private static final String[] ICON_COLORS = {
        "#5865f2","#ed4245","#23a55a","#f0b232","#eb459e",
        "#9b59b6","#3498db","#e67e22","#1abc9c","#e74c3c"
    };

    private static final String[][] DEFAULT_CHANNELS = {
        {"general",    "TEXT",  "Kênh chat chung cho tất cả mọi người", "0"},
        {"random",     "TEXT",  "Nói chuyện linh tinh",                  "1"},
        {"help",       "TEXT",  "Hỗ trợ và hướng dẫn",                   "2"},
        {"voice-chat", "VOICE", "Kênh thoại chung",                       "3"},
    };

    public ServerService(ServerRepository serverRepository,
                         ServerMemberRepository memberRepository,
                         ChannelRepository channelRepository) {
        this.serverRepository = serverRepository;
        this.memberRepository = memberRepository;
        this.channelRepository = channelRepository;
    }

    // ── Tạo server mới ────────────────────────────────────────────────
    public Map<String, Object> createServer(String name, String ownerId, String ownerName) {
        Server server = new Server();
        server.setName(name);
        server.setOwnerId(ownerId);
        server.setOwnerName(ownerName);
        server.setIconColor(randomColor(name));
        server.setInviteCode(generateInviteCode());
        server.setCreatedAt(LocalDateTime.now());
        Server saved = serverRepository.save(server);

        // Tạo các channel mặc định
        for (String[] ch : DEFAULT_CHANNELS) {
            ChannelEntity channel = new ChannelEntity();
            channel.setServerId(saved.getId());
            channel.setName(ch[0]);
            channel.setType(ch[1]);
            channel.setDescription(ch[2]);
            channel.setOrderIndex(Integer.parseInt(ch[3]));
            channelRepository.save(channel);
        }

        // Thêm owner vào danh sách member
        ServerMember owner = new ServerMember();
        owner.setServerId(saved.getId());
        owner.setUserId(ownerId);
        owner.setUsername(ownerName);
        owner.setRole("OWNER");
        owner.setJoinedAt(LocalDateTime.now());
        memberRepository.save(owner);

        return serverToMap(saved);
    }

    // ── Lấy danh sách server của user ────────────────────────────────
    public List<Map<String, Object>> getMyServers(String uid) {
        List<Server> servers = serverRepository.findServersByUserId(uid);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Server s : servers) {
            Map<String, Object> map = serverToMap(s);
            // Thêm member count
            long memberCount = memberRepository.findByServerId(s.getId()).size();
            map.put("memberCount", memberCount);
            result.add(map);
        }
        return result;
    }

    // ── Tham gia server bằng invite code ──────────────────────────────
    public Map<String, Object> joinServer(String inviteCode, String uid, String username) {
        Optional<Server> opt = serverRepository.findByInviteCode(inviteCode.trim().toUpperCase());
        if (opt.isEmpty()) {
            throw new RuntimeException("Mã mời không hợp lệ!");
        }
        Server server = opt.get();

        // Kiểm tra đã là member chưa
        if (memberRepository.existsByServerIdAndUserId(server.getId(), uid)) {
            return serverToMap(server); // Đã tham gia rồi, trả về server info
        }

        ServerMember member = new ServerMember();
        member.setServerId(server.getId());
        member.setUserId(uid);
        member.setUsername(username);
        member.setRole("MEMBER");
        member.setJoinedAt(LocalDateTime.now());
        memberRepository.save(member);

        return serverToMap(server);
    }

    // ── Lấy danh sách channels trong server ──────────────────────────
    public List<Map<String, Object>> getChannels(Long serverId) {
        List<ChannelEntity> channels = channelRepository.findByServerIdOrderByOrderIndexAsc(serverId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ChannelEntity ch : channels) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", ch.getId());
            map.put("serverId", ch.getServerId());
            map.put("name", ch.getName());
            map.put("type", ch.getType());
            map.put("description", ch.getDescription());
            map.put("orderIndex", ch.getOrderIndex());
            result.add(map);
        }
        return result;
    }

    // ── Lấy danh sách members trong server ───────────────────────────
    public List<Map<String, Object>> getMembers(Long serverId) {
        List<ServerMember> members = memberRepository.findByServerId(serverId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ServerMember m : members) {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", m.getUserId());
            map.put("username", m.getUsername());
            map.put("role", m.getRole());
            result.add(map);
        }
        return result;
    }

    // ── Lấy invite code của server ───────────────────────────────────
    public String getInviteCode(Long serverId, String uid) {
        Server server = serverRepository.findById(serverId)
            .orElseThrow(() -> new RuntimeException("Server không tồn tại!"));
        // Chỉ member mới được xem invite code
        if (!memberRepository.existsByServerIdAndUserId(serverId, uid)) {
            throw new RuntimeException("Bạn không phải thành viên của server này!");
        }
        return server.getInviteCode();
    }

    // ── Kiểm tra user có phải member không ───────────────────────────
    public boolean isMember(Long serverId, String uid) {
        return memberRepository.existsByServerIdAndUserId(serverId, uid);
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private Map<String, Object> serverToMap(Server s) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", s.getId());
        map.put("name", s.getName());
        map.put("ownerId", s.getOwnerId());
        map.put("ownerName", s.getOwnerName());
        map.put("iconColor", s.getIconColor());
        map.put("inviteCode", s.getInviteCode());
        return map;
    }

    private String generateInviteCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
        String code = sb.toString();
        // Đảm bảo unique
        while (serverRepository.findByInviteCode(code).isPresent()) {
            sb = new StringBuilder(8);
            for (int i = 0; i < 8; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
            code = sb.toString();
        }
        return code;
    }

    private String randomColor(String name) {
        int idx = Math.abs(name.hashCode()) % ICON_COLORS.length;
        return ICON_COLORS[idx];
    }
}
