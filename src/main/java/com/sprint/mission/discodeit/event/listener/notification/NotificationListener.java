package com.sprint.mission.discodeit.event.listener.notification;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 다양한 도메인 이벤트를 구독하여 사용자에게 알림을 생성하는 리스너입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final NotificationService notificationService;
  private final UserRepository userRepository;

  @Async("ioTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleMessageCreated(MessageCreatedEvent event) {  // on에서 가독성 향상을 위해 임의적으로 변경
    Message message = messageRepository.findById(event.messageId()).orElseThrow();
    Channel channel = message.getChannel();
    User author = message.getAuthor();

    List<ReadStatus> targetReadStatuses = readStatusRepository.findAllByChannelIdAndNotificationEnabled(channel.getId(), true);

    String title = String.format("%s (#%s)", author.getUsername(), channel.getName());
    String content = message.getContent();

    targetReadStatuses.stream()
        .map(ReadStatus::getUser)
        .filter(user -> !user.getId().equals(author.getId()))
        .forEach(user -> sendNotification(user.getId(), title, content));
  }

  @Async("ioTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleRoleUpdated(RoleUpdatedEvent event) {  // on에서 가독성 향상을 위해 임의적으로 변경
    String title = "권한이 변경되었습니다.";
    String content = String.format("%s -> %s", event.oldRole(), event.newRole());
    sendNotification(event.userId(), title, content);
  }

  @Async("ioTaskExecutor")
  @EventListener
  public void handleS3UploadFailed(S3UploadFailedEvent event) {
    String messageContent = String.format(
            """
            RequestId: %s
            BinaryContentId: %s
            Error: %s
            """,
        event.mdcRequestId(), event.binaryContentId(), event.errorMessage()
    );

    userRepository.findByRole(Role.ADMIN).forEach(admin -> 
      sendNotification(admin.getId(), "S3 파일 업로드 실패", messageContent)
    );
  }

  private void sendNotification(UUID userId, String title, String content){
    notificationService.create(userId, title, content);
  }
}
