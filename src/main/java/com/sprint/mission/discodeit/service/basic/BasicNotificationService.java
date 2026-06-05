package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationAccessDeniedException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 알림 관련 비즈니스 로직을 처리하는 기본 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final NotificationMapper notificationMapper;

  /**
   * 특정 사용자의 모든 알림 목록을 최신순으로 조회합니다.
   * 결과는 사용자별 캐시에 저장됩니다.
   *
   * @param userId 사용자 ID
   * @return 알림 목록
   */
  @Cacheable(value = "userNotificationsCache", key = "#userId")
  @Transactional(readOnly = true)
  @Override
  public List<NotificationDto> findAllByUserId(UUID userId) {
    List<NotificationDto> notifications = notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(userId).stream()
        .map(notificationMapper::toDto)
        .toList();
    
    log.debug("[Notification] 사용자 알림 조회: UserId={}, Count={}", userId, notifications.size());
    return notifications;
  }

  /**
   * 새로운 알림을 생성합니다.
   * 생성 시 해당 사용자의 알림 캐시를 무효화합니다.
   *
   * @param userId 수신자 ID
   * @param title 알림 제목
   * @param content 알림 내용
   */
  @CacheEvict(value = "userNotificationsCache", key = "#userId")
  @Transactional
  @Override
  public void create(UUID userId, String title, String content) {
    User receiver = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    Notification notification = new Notification(receiver, title, content);
    notificationRepository.save(notification);
    
    log.info("[Notification] 알림 생성 완료: ReceiverId={}, Title={}", userId, title);
  }

  /**
   * 특정 알림을 삭제합니다.
   *
   * @param userId 수신자 ID (본인 확인용)
   * @param notificationId 삭제할 알림 ID
   */
  @CacheEvict(value = "userNotificationsCache", key = "#userId")
  @Transactional
  @Override
  public void delete(UUID userId, UUID notificationId) {
    Notification notification = notificationRepository.findByIdAndReceiverId(notificationId, userId)
        .orElseThrow(() -> NotificationAccessDeniedException.withId(notificationId, userId));

    notificationRepository.delete(notification);
    log.info("[Notification] 알림 삭제 완료: ID={}, ReceiverId={}", notificationId, userId);
  }
}