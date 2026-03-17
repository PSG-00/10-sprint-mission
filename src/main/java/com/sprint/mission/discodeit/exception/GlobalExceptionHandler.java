package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 주로 서비스 단에서 던지는 예외 발생 시
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400 반환
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    // 객체를 찾을 수 없는 경우 발생
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException e) {
        log.error("NoSuchElementException: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404 반환
                .body(ErrorResponse.of(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    // @Valid 검증 실패 시 발생 (DTO에서 유효성 검사 에러)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("MethodArgumentNotValidException: {}", message, e);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), message));
    }

    // 주로 UUID 형식 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("JSON 파싱 실패: {}", e.getMessage());

        String errorMessage = "입력 형식이나 타입이 올바르지 않습니다. (예: UUID 형식 오류)";

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

    // 용량 초과 예외 핸들러
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        int status = HttpStatus.PAYLOAD_TOO_LARGE.value();
        String message = "업로드 가능한 최대 용량을 초과했습니다. (개별 파일 최대 10MB, 총합 50MB 이하만 가능)";
        ErrorResponse errorResponse = ErrorResponse.of(status, message);
        log.warn("⚠️ 파일 용량 초과 발생: {}", e.getMessage());

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }

    // 파일 저장 실패 등 서버 내부에서 제어할 수 없는 입출력 오류 처리
    @ExceptionHandler(IOException.class)  // 500 반환
    public ResponseEntity<ErrorResponse> handleIOException(IOException e) {
        log.error("IOException: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
    }

    // 서비스에서 IOException을 감싸서 던진 경우
    @ExceptionHandler(UncheckedIOException.class) // 자바 표준 런타임 IO 예외
    public ResponseEntity<ErrorResponse> handleUncheckedIOException(UncheckedIOException e) {
        log.error("UncheckedIOException: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
    }

    // 기타 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception e) {
        log.error("Exception: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "알 수 없는 오류가 발생했습니다."));
    }
}