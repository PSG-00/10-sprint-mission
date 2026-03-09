package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    List<ReadStatus> findAllByChannelId(UUID channelId);

    List<ReadStatus> findAllByUserId(UUID userId);

    @Query("""
    SELECT rs.channel.id
    FROM ReadStatus rs
    WHERE rs.user.id = :userId
""")
    List<UUID> findChannelIdsByUserId(@Param("userId") UUID userId);

    @Query("""
    select rs
    from ReadStatus rs
    join fetch rs.user u
    join fetch u.status
    left join fetch u.profile
    join fetch rs.channel
    where rs.channel.id in :channelIds
""")
    List<ReadStatus> findAllByChannelIdsWithUser(@Param("channelIds") List<UUID> channelIds);

    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

    void deleteByChannelId(UUID channelId);
}
