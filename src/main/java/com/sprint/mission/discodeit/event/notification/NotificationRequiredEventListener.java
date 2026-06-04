package com.sprint.mission.discodeit.event.notification;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.NotificationInMemoryRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final NotificationService notificationService; // 👈 Service 주입
  private final UserRepository userRepository;


  @Async("ioTaskExecutor")
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    // 1. Message, Channel, User(발신자) 정보를 한 번에 조회 (fetch join)
    Message message = messageRepository.findById(event.messageId())
        .orElseThrow();

    Channel channel = message.getChannel();
    User author = message.getAuthor();

    // 2. 해당 채널의 알림 여부를 활성화한 ReadStatus 조회
    List<ReadStatus> targetReadStatuses = readStatusRepository.findAllByChannelIdAndNotificationEnabled(channel.getId(), true);

    // 3. 알림 생성 로직 수행
    String title = String.format("%s (#%s)", author.getUsername(), channel.getName());
    String content = message.getContent();

    targetReadStatuses.stream()
        .map(ReadStatus::getUser)
        .filter(user -> !user.getId().equals(author.getId())) // 메시지 발신자 제외
        .forEach(user -> sendNotification(user.getId(), title, content));

  }

  @Async("ioTaskExecutor")
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    String title = "권한이 변경되었습니다.";
    String content = String.format("%s -> %s", event.oldRole(), event.newRole());
    sendNotification(event.userId(), title, content);

  }

  @Async("ioTaskExecutor")
  @EventListener
  public void on(S3UploadFailedEvent event) {
    String messageContent = String.format(
            """
            RequestId: %s
            BinaryContentId: %s
            Error: %s
            """,
        event.mdcRequestId(), event.binaryContentId(), event.errorMessage()
    );

    List<User> admins = userRepository.findByRole(Role.ADMIN);
    for (User admin : admins) {
      sendNotification(admin.getId(), "S3 파일 업로드 실패", messageContent);
    }
  }

  private void sendNotification(UUID userId, String title, String content){
    notificationService.create(userId, title, content);
  }
}
