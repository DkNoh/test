package com.example.sms.vo.sms;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SmsHistoryVO {
    private long          rowNum;
    private Long          id;
    private String        campaignName;
    private String        sendType;
    private String        senderNo;
    private String        receiverNo;
    private String        message;
    private String        sendStatus;
    private String        resendYn;
    private String        resultCode;
    private String        resultMessage;
    private String        telecom;
    private LocalDateTime reservedAt;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
