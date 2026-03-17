package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public BinaryContentDto.CreateRequest multipartFileToCreateRequest(MultipartFile file) {
        try {
            return new BinaryContentDto.CreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new UncheckedIOException("파일 읽기 오류", e);
        }
    }

    @Override
    @Transactional
    public BinaryContentDto.Response create(BinaryContentDto.CreateRequest request) {
        String fileName = request.fileName();
        String contentType = request.contentType();
        byte[] bytes = request.bytes();

        BinaryContent binaryContent = new BinaryContent(fileName, contentType, bytes.length);

        BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

        binaryContentStorage.put(savedBinaryContent.getId(), bytes);

        return binaryContentMapper.toResponse(savedBinaryContent);
    }

    @Override
    public BinaryContentDto.Response find(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .map(binaryContentMapper::toResponse)
                .orElseThrow(() -> new NoSuchElementException("해당 파일을 찾을 수 없습니다: " + binaryContentId));
    }

    @Override
    public List<BinaryContentDto.Response> findAllByIn(List<UUID> binaryContentIds) {
        List<BinaryContent> binaryContents = binaryContentRepository.findAllByIdIn(binaryContentIds);

        if (binaryContents.size() != binaryContentIds.size()) {
            log.warn("첨부파일 유실됨");
            throw new NoSuchElementException("일부 첨부파일 데이터가 유실되었습니다. 확인이 필요합니다!");
        }

        return binaryContents.stream()
                .map(binaryContentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("해당 파일을 찾을 수 없습니다: " + binaryContentId));

        binaryContentRepository.delete(binaryContent);
        binaryContentStorage.delete(binaryContentId);
    }
}
