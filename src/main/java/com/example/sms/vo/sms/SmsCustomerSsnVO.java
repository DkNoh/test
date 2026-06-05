package com.example.sms.vo.sms;

import lombok.Data;

@Data
public class SmsCustomerSsnVO {
    private long   rowNum;
    private String phoneNo;
    private String ssnMasked;
    private String holderName;
    private String deptNm;
    private String intermediary;
}
