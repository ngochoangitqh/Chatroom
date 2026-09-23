package com.voicehub.repository;
import com.voicehub.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<Friendship> findByUserLowUidAndUserHighUid(String low, String high);
    List<Friendship> findByStatusAndUserLowUidOrStatusAndUserHighUid(Friendship.Status a, String low, Friendship.Status b, String high);
    List<Friendship> findByStatusAndUserHighUid(Friendship.Status status, String high);
}
