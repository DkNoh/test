package com.example.sms.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * [SecurityUtil]
 * 스프링 시큐리티 컨텍스트에서 현재 인증된 사용자(로그인한 사용자)의 정보를 
 * 손쉽게 추출하기 위한 전역 유틸리티 클래스입니다.
 * 
 * [사용법(Usage)]
 * 1. 현재 로그인한 사용자 아이디 추출:
 *    String userId = SecurityUtil.getCurrentUserId();
 * 
 * 2. 현재 로그인 여부 확인:
 *    boolean isLogin = SecurityUtil.isAuthenticated();
 */
public class SecurityUtil {

    // 인스턴스화 방지 (정적 메서드만 제공)
    private SecurityUtil() {
    }

    /**
     * 현재 로그인한 사용자의 ID(username)를 반환합니다.
     * 로그인이 되어있지 않거나 인증 정보가 없으면 "system" 또는 익명 사용자 아이디를 반환할 수 있습니다.
     *
     * @return 현재 로그인한 사용자의 ID
     */
    public static String getCurrentUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            return "system"; // 스케줄러나 시스템 내부 로직인 경우 기본값
        }

        return authentication.getName();
    }
    
    /**
     * 현재 사용자가 로그인한 상태인지 확인합니다.
     * 
     * @return 로그인 여부 (true/false)
     */
    public static boolean isAuthenticated() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || "anonymousUser".equals(authentication.getName())) {
            return false;
        }
        return authentication.isAuthenticated();
    }
}
