package com.example.sms.dto.contact;

import lombok.Data;

@Data
public class ContactVO {
    private long rowNum;
    private String contactId;
    private String contactNm;
    private String phoneNo;
    private String companyNm;
    private String groupId;
    private String regDt;
}
