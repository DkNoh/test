package com.example.sms.vo.sms;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CampaignHistoryVO {
    private long          rowNum;
    private Long          campaignId;   // 번호
    private String        campaignName; // 캠페인명
    private String        regId;        // 등록ID (TODO: CAMPAIGN 테이블에 없음)
    private LocalDateTime sentAt;       // 전송일시
    private String        message;      // 내용
    private Long          sendCount;    // 발송건수 (SMS_HISTORY COUNT)
    private String        sendType;     // 구분 (SMS/LMS/ALIMTALK)
    private String        status;       // 예약/상태
}
