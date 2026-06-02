package com.example.sms.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 이 어노테이션이 붙은 메서드는 실행 시 자동으로 AOP를 통해 
 * privacy_audit_log 테이블에 민감 정보 조회/변경 이력이 기록됩니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrivacyLog {
    /**
     * 어떤 업무를 수행했는지 명시 (예: "고객별 발송내역 조회 (전화번호)")
     */
    String action() default "민감 정보 조회";
}