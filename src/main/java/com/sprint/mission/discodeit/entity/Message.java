package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "messages")
public class Message extends BaseUpdateEntity {

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    //
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", columnDefinition = "uuid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", columnDefinition = "uuid")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User author;
    //
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true) // message_attachments가 cascade에 적용됨
    @JoinTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_id", columnDefinition = "uuid", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "attachment_id", columnDefinition = "uuid", nullable = false)
    )
    @BatchSize(size = 50)
    private List<BinaryContent> attachments = new ArrayList<>();

    public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
        this.content = (content == null) ? "" : content;
        this.channel = channel;
        this.author = author;
        if (attachments != null) this.attachments.addAll(attachments);
    }

    public void update(String newContent) {
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
        }
    }
}
