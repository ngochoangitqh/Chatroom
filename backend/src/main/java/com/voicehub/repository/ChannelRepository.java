package com.voicehub.repository;

import com.voicehub.model.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelRepository extends JpaRepository<ChannelEntity, Long> {
    List<ChannelEntity> findByServerIdOrderByOrderIndexAsc(Long serverId);
}
