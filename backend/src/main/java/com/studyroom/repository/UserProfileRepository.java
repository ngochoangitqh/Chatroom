package com.studyroom.repository;
import com.studyroom.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByFirebaseUid(String firebaseUid);
    Optional<UserProfile> findByEmailIgnoreCase(String email);
}

