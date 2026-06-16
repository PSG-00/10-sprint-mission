package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicSseService implements SseService {

    private static final Long DEFAULT_TIMEOUT = 1000L * 60 * 30; // 30분
    private final SseEmitterRepository sseEmitterRepository;
    private final SseMessageRepository sseMessageRepository;

    @Override
    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        // 새로운 연결 전에 해당 유저의 죽은 연결들 미리 정리
        List<SseEmitter> existingEmitters = sseEmitterRepository.findAllByUserId(receiverId);
        if (!existingEmitters.isEmpty()) {
            existingEmitters.removeIf(emitter -> !ping(emitter));
        }

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitter.onTimeout(() -> sseEmitterRepository.remove(receiverId, emitter));
        emitter.onError((e) -> sseEmitterRepository.remove(receiverId, emitter));
        emitter.onCompletion(() -> sseEmitterRepository.remove(receiverId, emitter));

        sseEmitterRepository.add(receiverId, emitter);

        // 유실 복원 처리
        if (lastEventId != null) {
            List<SseMessage> missedMessages = sseMessageRepository.findAllByReceiverIdAndAfterId(receiverId, lastEventId);
            missedMessages.forEach(msg -> sendToClient(emitter, msg.eventName(), msg));
        }

        // 초기 연결 확인용 ping
        ping(emitter);

        int userConnectionCount = sseEmitterRepository.findAllByUserId(receiverId).size();
        int totalEmitterCount = sseEmitterRepository.findAll().values().stream()
                .mapToInt(Collection::size)
                .sum();

        log.info("[SSE] 신규 연결 등록: UserId={}, 유저별연결수={}, 전체에미터수={}", 
                receiverId, userConnectionCount, totalEmitterCount);
        return emitter;
    }

    @Override
    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        SseMessage message = sseMessageRepository.save(receiverIds, eventName, data);

        receiverIds.stream()
                .map(sseEmitterRepository::findAllByUserId)
                .flatMap(Collection::stream)
                .parallel()
                .forEach(emitter -> sendToClient(emitter, eventName, message));
    }

    @Override
    public void broadcast(String eventName, Object data) {
        SseMessage message = sseMessageRepository.save(java.util.List.of(), eventName, data);

        sseEmitterRepository.findAll().values().stream()
                .flatMap(Collection::stream)
                .parallel()
                .forEach(emitter -> sendToClient(emitter, eventName, message));
    }

    @Scheduled(fixedDelay = 1000 * 45) // 45초마다 점검
    @Override
    public void cleanUp() {
        log.info("[SSE] 연결 상태 점검 시작...");
        sseEmitterRepository.findAll().forEach((userId, emitters) -> {
            emitters.removeIf(emitter -> !ping(emitter));
        });
        log.info("[SSE] 연결 상태 점검 완료.");
    }

    private boolean ping(SseEmitter sseEmitter) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("ping")
                    .data("keep-alive"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void sendToClient(SseEmitter emitter, String eventName, Object data) {
        try {
            SseEmitter.SseEventBuilder eventBuilder = SseEmitter.event()
                    .name(eventName);

            if (data instanceof SseMessage sseMessage) {
                eventBuilder.id(sseMessage.id().toString());
                eventBuilder.data(sseMessage.data());
            } else {
                eventBuilder.data(data);
            }

            emitter.send(eventBuilder);
            log.debug("[SSE] 전송 성공: Event={}", eventName);
        } catch (IOException e) {
            log.warn("[SSE] 전송 실패 (연결 끊김): Event={}, Error={}", eventName, e.getMessage());
            emitter.complete();
        } catch (Exception e) {
            log.error("[SSE] 알 수 없는 전송 에러: Event={}, Error={}", eventName, e.getMessage(), e);
            emitter.complete();
        }
    }
}
