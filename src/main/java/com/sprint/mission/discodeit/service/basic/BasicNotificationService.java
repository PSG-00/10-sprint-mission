package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationAccessDeniedException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;

  // ⭐️ 조회 시 캐싱 적용 (키: 유저 ID)
  @Cacheable(value = "userNotificationsCache", key = "#userId")
  @Transactional(readOnly = true)
  @Override
  public List<NotificationDto> findAllByUserId(UUID userId) {
    return notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(userId).stream()
        .map(this::toDto)
        .toList();
  }

  // ⭐️ 알림 생성 시 캐시 무효화
  @CacheEvict(value = "userNotificationsCache", key = "#userId")
  @Transactional
  @Override
  public void create(UUID userId, String title, String content) {
    User receiver = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    Notification notification = new Notification(receiver, title, content);
    notificationRepository.save(notification);
  }

  // ⭐️ 알림 삭제 시 캐시 무효화
  @CacheEvict(value = "userNotificationsCache", key = "#userId")
  @Transactional
  @Override
  public void delete(UUID userId, UUID notificationId) {
    if (!notificationRepository.existsById(notificationId)) {
      throw NotificationNotFoundException.withId(notificationId);
    }

    Notification notification = notificationRepository.findByIdAndReceiverId(notificationId, userId)
        .orElseThrow(() -> NotificationAccessDeniedException.withId(notificationId, userId));

    notificationRepository.delete(notification);
  }

  private NotificationDto toDto(Notification notification) {
    return new NotificationDto(
        notification.getId(),
        notification.getCreatedAt(),
        notification.getReceiver().getId(),
        notification.getTitle(),
        notification.getContent()
    );
  }
}