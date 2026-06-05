package com.example.sms.vo.sms;

import lombok.Data;

@Data
public class DeptStatVO {
    private String timeSlot;     // 시간대 (00시 ~ 23시)
    private Long   sendingCount; // 전송중 (WAIT)
    private Long   failCount;    // 실패
    private Long   successCount; // 성공
    private Long   totalCount;   // 총건수
}
