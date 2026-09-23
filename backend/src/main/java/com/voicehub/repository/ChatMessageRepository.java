package com.voicehub.repository;

import com.voicehub.model.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    // Lấy 50 tin nhắn gần nhất của một channel (theo tên cũ — backward compat)
    List<ChatMessageEntity> findTop50ByChannelOrderByCreatedAtAsc(String channel);

    // Lấy 50 tin nhắn gần nhất của một channelId (dùng DB ID)
    List<ChatMessageEntity> findTop50ByChannelIdOrderByCreatedAtAsc(Long channelId);

    // Lấy N tin nhắn trước 1 id nhất định (phục vụ load more) — theo channelId
    List<ChatMessageEntity> findTop30ByChannelIdAndIdLessThanOrderByCreatedAtDesc(
        Long channelId, Long beforeId
    );
}
