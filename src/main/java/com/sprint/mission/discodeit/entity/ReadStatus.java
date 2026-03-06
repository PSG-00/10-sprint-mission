package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(
        name = "read_statuses",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_read_statuses_user_channel",
                columnNames = {"user_id", "channel_id"}
        )
)
public class ReadStatus extends BaseUpdateEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "uuid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", columnDefinition = "uuid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Channel channel;

    @Column(name = "last_read_at", nullable = false)
    private Instant lastReadAt;

    public ReadStatus(User user, Channel channel, Instant lastReadAt) {
        this.user = user;
        this.channel = channel;
        //this.lastReadAt = Instant.EPOCH; // 1970년, 유저 생성 이전에 생성된 메시지 안읽음 처리
        this.lastReadAt = lastReadAt;
    }

    public void update(Instant newLastReadAt) {
        if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
        }
    }

    public boolean hasRead(Instant messageCreatedAt) {
        return !messageCreatedAt.isAfter(this.lastReadAt);
    }

}
