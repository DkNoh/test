package com.example.sms.exception;

import com.example.sms.dto.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * [GlobalExceptionHandler]
 * 프로젝트 내의 모든 @RestController에서 발생하는 예외를 낚아채서(Catch)
 * ErrorResponse 규격에 맞춰 JSON 형태로 반환해주는 전역 에러 처리기입니다.
 * 
 * [사용법(Usage)]
 * - 개발자가 명시적으로 이 클래스를 호출할 필요는 없습니다.
 * - 컨트롤러 내부 로직 수행 중 에러(예: CustomException, Validation 실패 등)가 발생하면
 *   스프링이 자동으로 이 클래스의 핸들러 메서드를 호출하여 프론트엔드로 일관된 에러 JSON을 내려보냅니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * [1] 비즈니스 로직에서 의도적으로 발생시킨 CustomException 처리
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        log.error("handleCustomException", e);
        ErrorCode errorCode = e.getErrorCode();
        
        ApiResponse<Void> response = ApiResponse.error(errorCode.getStatus().value(), errorCode.getMessage());
                
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    /**
     * [2] @Valid 또는 @Validated 객체 검증 실패(예: 필수값 누락) 시 발생하는 에러 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        
        // 첫 번째 에러 메시지 추출
        String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
        
        // 검증 에러는 시스템 에러가 아니므로, 장황한 스택 트레이스 대신 심플한 WARN 로그만 남김
        log.warn("입력값 검증 실패 (Validation Error): {}", errorMessage);
        
        ApiResponse<Void> response = ApiResponse.error(
                ErrorCode.INVALID_INPUT_VALUE.getStatus().value(),
                errorMessage != null ? errorMessage : ErrorCode.INVALID_INPUT_VALUE.getMessage()
        );
                
        return new ResponseEntity<>(response, ErrorCode.INVALID_INPUT_VALUE.getStatus());
    }

    /**
     * [3] 그 외에 서버 내부에서 발생하는 모든 예상치 못한 에러(NullPointerException 등) 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("handleException", e);
        
        ApiResponse<Void> response = ApiResponse.error(
                ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + " (" + e.getMessage() + ")"
        );
                
        return new ResponseEntity<>(response, ErrorCode.INTERNAL_SERVER_ERROR.getStatus());
    }
}
