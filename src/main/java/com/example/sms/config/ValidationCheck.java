package com.example.sms.config;

/**
 * 전사 시스템의 유효성 검증 규약 상수를 중앙 관리하는 클래스
 */
public class ValidationCheck {
    
    // 사원번호(조회자 ID) 관련 규약
    public static final int EMP_ID_MIN = 4;
    public static final int EMP_ID_MAX = 10;
    public static final String EMP_ID_MSG = "사원번호는 " + EMP_ID_MIN + "자 이상 " + EMP_ID_MAX + "자 이하로 입력해야 합니다.";

    // SMS 검색 키워드 관련 규약
    public static final int KEYWORD_MAX = 20;
    public static final String KEYWORD_MSG = "검색 키워드는 최대 " + KEYWORD_MAX + "자까지만 입력 가능합니다.";

    // 고객 ID 관련 규약
    public static final int CUSTOMER_ID_MIN = 5;
    public static final int CUSTOMER_ID_MAX = 15;
    public static final String CUSTOMER_ID_MSG = "고객 ID는 " + CUSTOMER_ID_MIN + "자 이상 " + CUSTOMER_ID_MAX + "자 이하로 입력해야 합니다.";
}