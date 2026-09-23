package com.voicehub.repository;
import com.voicehub.model.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {
    List<DirectMessage> findTop100ByConversationKeyOrderByCreatedAtAsc(String conversationKey);
}
