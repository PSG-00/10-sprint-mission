package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Optional;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "channels")
public class Channel extends BaseUpdateEntity {

    @Enumerated(EnumType.STRING)
    private ChannelType type;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
    //
    @Column(name = "last_message_at")
    private Instant lastMessageAt;

    public Channel(ChannelType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.lastMessageAt = null;
    }

    public void update(String newName, String newDescription) {
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
        }
    }

    public void updateLastMessageAt(Instant lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }
}
