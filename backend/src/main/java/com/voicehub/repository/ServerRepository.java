package com.voicehub.repository;

import com.voicehub.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long> {
    Optional<Server> findByInviteCode(String inviteCode);

    // Lấy tất cả servers mà user là member
    @Query("SELECT s FROM Server s WHERE s.id IN " +
           "(SELECT sm.serverId FROM ServerMember sm WHERE sm.userId = :userId)")
    List<Server> findServersByUserId(String userId);
}
