package com.sprint.mission.discodeit.event.binaryContent;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService; // 서비스 주입

  @Async("ioTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentCreatedEvent(BinaryContentCreatedEvent event) {
    try {
      // 1. S3 (또는 로컬 스토리지)에 바이너리 파일 업로드 시도
      binaryContentStorage.put(event.binaryContentId(), event.bytes());
      System.out.println("[BinaryContent Upload Success1] ID: " + event.binaryContentId());

      // 2. 성공 시 SUCCESS 상태로 업데이트
      binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);
      System.out.println("[BinaryContent Upload Success2] ID: " + event.binaryContentId());

    } catch (Exception e) {
      log.error("[BinaryContent Upload Failed] ID: {}, Error: {}", event.binaryContentId(), e.getMessage());

      // 3. 예외 발생 시 FAIL 상태로 업데이트
      binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
    }
  }
}