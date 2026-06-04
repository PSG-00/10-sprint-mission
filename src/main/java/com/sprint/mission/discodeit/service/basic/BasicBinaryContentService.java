package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.etc.FileProcessingException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.event.binaryContent.BinaryContentCreatedEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentMapper binaryContentMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public BinaryContentDto.CreateRequest multipartFileToCreateRequest(MultipartFile file) {
        try {
            return new BinaryContentDto.CreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw FileProcessingException.readFailed(file.getOriginalFilename(), e);
        }
    }

    @Override
    @Transactional
    public BinaryContentDto.Response create(BinaryContentDto.CreateRequest request) {
        String fileName = request.fileName();
        String contentType = request.contentType();
        byte[] bytes = request.bytes();

        // 1. 상태를 PROCESSING(또는 UPLOADING)으로 DB에 메타데이터 저장
        BinaryContent savedBinaryContent = binaryContentRepository.save(
            new BinaryContent(
                fileName,
                contentType,
                bytes.length,
                BinaryContentStatus.PROCESSING)
        );

        // 2. 이벤트 발행
        eventPublisher.publishEvent(
            new BinaryContentCreatedEvent(savedBinaryContent.getId(), request.bytes())
        );

        // 3. 메타데이터 응답 반환 (메소드가 끝나면서 커밋되어 트랜잭션 종료)
        return binaryContentMapper.toResponse(savedBinaryContent);
    }

    @Override
    public BinaryContentDto.Response find(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .map(binaryContentMapper::toResponse)
                .orElseThrow(() -> BinaryContentNotFoundException.withId(binaryContentId));
    }

    @Override
    public List<BinaryContentDto.Response> findAllByIn(List<UUID> binaryContentIds) {
        Set<UUID> uniqueIds = new HashSet<>(binaryContentIds);
        List<BinaryContent> binaryContents = binaryContentRepository.findAllByIdIn(uniqueIds);

        if (binaryContents.size() != uniqueIds.size()) {
            Set<UUID> foundIds = binaryContents.stream()
                    .map(BinaryContent::getId)
                    .collect(Collectors.toSet());

            List<UUID> missingIds = uniqueIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw BinaryContentNotFoundException.withIds(uniqueIds.size(), missingIds);
        }

        return binaryContents.stream()
                .map(binaryContentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> BinaryContentNotFoundException.withId(binaryContentId));

        binaryContentRepository.delete(binaryContent);
    }

    @Override
    @Transactional // 새로운 스레드에서 호출되므로 트랜잭션 시작
    public BinaryContentDto.Response updateStatus(UUID binaryContentId, BinaryContentStatus status) {
        // 1. DB에서 새롭게 조회 (영속성 컨텍스트에 올리기 위함)
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> BinaryContentNotFoundException.withId(binaryContentId));

        // 2. 상태 업데이트
        binaryContent.updateStatus(status);
        binaryContentRepository.saveAndFlush(binaryContent);

        // (JPA의 Dirty Checking 기능 덕분에 메서드가 끝나면서 자동으로 DB에 Update 쿼리가 날아갑니다)
        return binaryContentMapper.toResponse(binaryContent);
    }
}
