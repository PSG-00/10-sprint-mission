package com.sprint.mission.discodeit.event.listener.notification;

import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.service.NotificationEventService;
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

  private final NotificationEventService notificationEventService;

  @Async("ioTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleMessageCreated(MessageCreatedEvent event) {
    notificationEventService.sendByMessageCreated(event.messageId());
  }

  @Async("ioTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleRoleUpdated(RoleUpdatedEvent event) {
    notificationEventService.sendByRoleUpdated(event.userId(), event.oldRole(), event.newRole());
  }

  @Async("ioTaskExecutor")
  @EventListener
  public void handleS3UploadFailed(S3UploadFailedEvent event) {
    notificationEventService.sendS3UploadFailedNotification(
        event.binaryContentId(), 
        event.mdcRequestId(), 
        event.errorMessage()
    );
  }
}
