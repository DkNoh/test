package com.example.sms.exception;

import com.example.sms.dto.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** [1] 비즈니스 CustomException */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        log.error("handleCustomException", e);
        ErrorCode errorCode = e.getErrorCode();
        return new ResponseEntity<>(
            ApiResponse.error(errorCode.getStatus().value(), errorCode.getMessage()),
            errorCode.getStatus());
    }

    /** [2] @Valid 검증 실패 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
        log.warn("입력값 검증 실패: {}", errorMessage);
        return new ResponseEntity<>(
            ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE.getStatus().value(),
                errorMessage != null ? errorMessage : ErrorCode.INVALID_INPUT_VALUE.getMessage()),
            ErrorCode.INVALID_INPUT_VALUE.getStatus());
    }

    /**
     * [3] URL 매핑 없음 (404) — 컨트롤러가 없거나 메뉴 URL이 잘못된 경우
     *     상세 원인: 요청한 URL에 매핑된 컨트롤러/정적 리소스가 없음
     */
    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<ApiResponse<Void>> handleNoResourceFound(NoResourceFoundException e) {
        String url = e.getResourcePath();
        String message = String.format(
            "요청한 URL [%s]에 대한 화면이 없습니다. " +
            "원인: ① 컨트롤러 미등록 ② 메뉴 URL 오타 ③ 파일 미배치 중 하나를 확인하세요.", url);
        log.warn("NoResourceFoundException — URL: {}", url);
        return new ResponseEntity<>(
            ApiResponse.error(HttpStatus.NOT_FOUND.value(), message),
            HttpStatus.NOT_FOUND);
    }

    /** [4] 그 외 서버 내부 오류 */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("handleException", e);
        return new ResponseEntity<>(
            ApiResponse.error(
                ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " (" + e.getMessage() + ")"),
            ErrorCode.INTERNAL_SERVER_ERROR.getStatus());
    }
}
