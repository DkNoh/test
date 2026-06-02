package com.example.sms.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * [ApiResponse]
 * 프로젝트 내 모든 REST API의 응답을 감싸는(Wrap) 공통 규격(택배 상자) 클래스입니다.
 * 성공/실패 여부와 관계없이 프론트엔드는 항상 이 규격의 JSON을 받게 됩니다.
 * 
 * [사용법(Usage)]
 * 1. 성공 시 데이터 반환:
 *    return ResponseEntity.ok(ApiResponse.success(myDto));
 * 
 * 2. 에러 발생 시 반환 (GlobalExceptionHandler 등에서 사용):
 *    return ResponseEntity.status(400).body(ApiResponse.error(400, "잘못된 요청"));
 * 
 * @param <T> 실제 반환할 데이터의 타입
 */
@Getter
@Builder
@AllArgsConstructor
public class ApiResponse<T> {

    @Builder.Default
    private String timestamp = LocalDateTime.now().toString();

    private int code;       // 결과 코드 (예: 200, 400, 500)
    private String message; // 결과 메시지
    private T data;         // 실제 데이터 (에러일 경우 null)

    /**
     * 정상(200) 응답 객체 생성 (데이터 포함)
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("SUCCESS")
                .data(data)
                .build();
    }

    /**
     * 정상(200) 응답 객체 생성 (커스텀 메시지, 데이터 포함)
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * 에러 응답 객체 생성 (데이터 없음)
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}
