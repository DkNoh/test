package com.example.sms.dto.sms;

import lombok.Data;

@Data
public class HistoryTestVO {
    private long rowNum;
    private String msgId;
    private String senderId;
    private String receiverPhone;
    private String msgType;
    private String sendStatus;
    private String sendDt;
}
