package com.example.sms.vo.sms;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CampaignTargetVO {
    private long          rowNum;
    private Long          campaignId;    // 캠페인ID
    private String        campaignName;  // 발송대상명
    private LocalDateTime regDt;         // 등록일시
    private String        regId;         // 등록자
    private Long          targetCount;   // 대상수
    private Long          sendCount;     // 발송횟수
    private String        status;        // 승인여부 (WAIT/APPROVED/REJECTED)
    private String        aprvUser;      // 승인자
    private String        sendType;      // SMS/LMS/ALIMTALK
}
