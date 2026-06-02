package com.example.sms.dto.sms;

import lombok.Data;

@Data
public class DeptStatVO {
    private long rowNum;
    private String deptId;
    private String deptNm;
    private String totalSendCnt;
    private String totalSuccessCnt;
    private String totalFailCnt;
}
