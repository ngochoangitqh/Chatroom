package com.voicehub.repository;
import com.voicehub.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByFirebaseUid(String firebaseUid);
    Optional<UserProfile> findByEmailIgnoreCase(String email);
}
