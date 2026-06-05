package com.example.sms.vo.basic;

import lombok.Data;

@Data
public class MessageVO {
    private long   rowNum;
    private String deptNm;     // 부서명 (TB_DEPT 조인)
    private String msgCode;    // 코드
    private String forbidYn;   // 금지대상 (Y/N)
    private String msgTitle;   // 메세지제목
    private String regDt;      // 등록일자
    private String sendPoint;  // 발송시점
    private String msgContent; // 메세지내용
}
