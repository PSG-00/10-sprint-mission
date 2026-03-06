package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Channel c " +
            "WHERE c.id IN :channelIds " +
            "AND c.type = 'PRIVATE' " +
            "AND (SELECT COUNT(rs) FROM ReadStatus rs WHERE rs.channel = c) <= 1")
    void deleteEmptyOrLonelyChannels(@Param("channelIds") List<UUID> channelIds);
}
