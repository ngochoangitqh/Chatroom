package com.voicehub.service;

import com.voicehub.model.VoiceParticipant;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VoicePresenceService {
    private final ConcurrentHashMap<Long, ConcurrentHashMap<String, VoiceParticipant>> participantsByChannel = new ConcurrentHashMap<>();

    public List<VoiceParticipant> join(Long channelId, VoiceParticipant participant) {
        participantsByChannel
            .computeIfAbsent(channelId, ignored -> new ConcurrentHashMap<>())
            .put(participant.getUid(), participant);
        return getParticipants(channelId);
    }

    public List<VoiceParticipant> leave(Long channelId, String uid) {
        ConcurrentHashMap<String, VoiceParticipant> participants = participantsByChannel.get(channelId);
        if (participants == null) return List.of();
        participants.remove(uid);
        if (participants.isEmpty()) participantsByChannel.remove(channelId, participants);
        return getParticipants(channelId);
    }

    public List<VoiceParticipant> getParticipants(Long channelId) {
        ConcurrentHashMap<String, VoiceParticipant> participants = participantsByChannel.get(channelId);
        return participants == null ? List.of() : new ArrayList<>(participants.values());
    }
}
