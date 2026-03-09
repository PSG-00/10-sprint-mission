package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    @EntityGraph(attributePaths = {"author", "author.status", "author.profile", "channel"})
    Page<Message> findAllByChannelId(UUID channelId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "author.status", "author.profile", "channel"})
    Optional<Message> findById(@NonNull UUID messageId);


    void deleteByChannelId(UUID channelId);
}
