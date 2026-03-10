package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String path) {
        this.root = Path.of(path);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.root);
            log.info("저장소 초기화 성공! 경로: {}", root);
        } catch (IOException e) {
            log.error("저장소 폴더를 생성하는 도중 에러가 발생했습니다. 경로: {}", root, e);
            throw new IllegalStateException("저장소 폴더 생성 실패:  경로: {}" + root, e);
        }
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        Path path = resolvePath(binaryContentId);

        try {
            Files.write(path, bytes);
            return binaryContentId;
        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생", e);
            throw new IllegalStateException("파일 저장 실패", e);
        }
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        Path path = resolvePath(binaryContentId);

        if (Files.notExists(path)) {
            log.warn("파일을 찾을 수 없습니다. ID: {}, 경로: {}", binaryContentId, path.toAbsolutePath());
            throw new NoSuchElementException("해당 파일을 찾을 수 없습니다.");
        }

        try {
            return new BufferedInputStream(Files.newInputStream(path));
        } catch (IOException e) {
            log.error("파일을 읽는 도중 오류 발생. ID: {}", binaryContentId, e);
            throw new IllegalStateException("파일을 읽는 도중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto.Response response) {
        InputStream inputStream = get(response.id());
        Resource resource = new InputStreamResource(inputStream);

        String contentDisposition = ContentDisposition.attachment()
                .filename(response.fileName(), StandardCharsets.UTF_8)
                .build()
                .toString();

        return ResponseEntity.ok()
                .contentType(response.contentType() != null ?
                        MediaType.parseMediaType(response.contentType()) :
                        MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(response.size())
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    @Override
    public void delete(UUID binaryContentId) {
        Path path = resolvePath(binaryContentId);
        try {
            if (Files.deleteIfExists(path)) {
                log.info("파일 삭제 성공: {}", path);
            } else {
                log.warn("삭제할 파일이 존재하지 않습니다: {}", path);
            }
        } catch (IOException e) {
            throw new IllegalStateException("파일 삭제 중 오류가 발생했습니다: " + path, e);
        }
    }

    private Path resolvePath(UUID binaryContentId) {
        return root.resolve("LocalFile_" + binaryContentId.toString());
    }
}
