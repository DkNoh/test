package com.example.sms.vo.sms;

import lombok.Data;

@Data
public class campaignSearchVO {
    private long rowNum;
    private String campaignId;
    private String campaignName;
    private String sendType;
    private String status;
    private String totalCnt;
    private String successCnt;
    private String failCnt;
    private String pendingCnt;
    private String successRate;
}
