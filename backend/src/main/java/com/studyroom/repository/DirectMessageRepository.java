package com.studyroom.repository;
import com.studyroom.model.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {
    List<DirectMessage> findTop100ByConversationKeyOrderByCreatedAtAsc(String conversationKey);
}

