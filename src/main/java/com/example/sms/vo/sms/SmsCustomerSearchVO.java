package com.example.sms.vo.sms;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SmsCustomerSearchVO {
    private long          rowNum;
    private LocalDateTime sentAt;
    private LocalDateTime receivedAt;
    private String        holderName;
    private String        receiverNo;
    private String        senderNo;
    private String        message;
    private String        intermediary;
    private String        sendStatus;
    private String        deptNm;
}
