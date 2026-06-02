package com.example.sms.exception;

import lombok.Getter;

/**
 * [CustomException]
 * 비즈니스 로직(Service) 수행 중 예측 가능한 에러가 발생했을 때 강제로 예외를 던지기 위한 클래스입니다.
 * 
 * [사용법(Usage)]
 * 1. 로직 중 검증 실패 시:
 *    if (employee == null) {
 *        throw new CustomException(ErrorCode.USER_NOT_FOUND);
 *    }
 */
@Getter
public class CustomException extends RuntimeException {
    
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
