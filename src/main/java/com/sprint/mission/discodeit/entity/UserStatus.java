package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Duration;
import java.time.Instant;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdateEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "uuid", unique = true, nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "last_active_at", nullable = false)
    private Instant lastActiveAt;

    public UserStatus(User user, Instant lastActiveAt) {
        this.user = user;
        this.lastActiveAt = lastActiveAt;
    }

    public void update(Instant newLastActiveAt) {
        if (newLastActiveAt != null && !newLastActiveAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = newLastActiveAt;
        }
    }

    public boolean isOnline() {
        final int DURATION_IN_MINUTES = 5;
        Instant fiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(DURATION_IN_MINUTES));

         return lastActiveAt.isAfter(fiveMinutesAgo);
    }

}
