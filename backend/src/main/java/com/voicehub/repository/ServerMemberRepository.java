package com.voicehub.repository;

import com.voicehub.model.ServerMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerMemberRepository extends JpaRepository<ServerMember, Long> {
    List<ServerMember> findByServerId(Long serverId);
    Optional<ServerMember> findByServerIdAndUserId(Long serverId, String userId);
    boolean existsByServerIdAndUserId(Long serverId, String userId);
    List<ServerMember> findByUserId(String userId);
}
