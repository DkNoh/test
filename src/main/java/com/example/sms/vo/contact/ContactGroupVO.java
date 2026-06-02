package com.example.sms.vo.contact;

import lombok.Data;

@Data
public class ContactGroupVO {
    private long rowNum;
    private String groupId;
    private String groupNm;
    private String groupDesc;
    private String useYn;
    private String regId;
    private String regDt;
}