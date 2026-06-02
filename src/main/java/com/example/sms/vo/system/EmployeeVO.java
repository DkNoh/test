package com.example.sms.vo.system;

import lombok.Data;

@Data
public class EmployeeVO {
    private String empId;
    private String depId;
    private String depNm; // From SMS.DEP
    private String empNm;
    private String empLev;
    private String regDttm;
    private Integer maxSendCnt;
    private String permCpn;
    private String permSys;
    private String permSta;
    private Integer nowSendCnt;
    private String lastSendDt;
    private Integer loginFailCnt;
    private String empPhone;
    private String permPsn;
    private String permAut;
    private String permCpnAgree;
    private String delDttm;
    private String actYn;
    private String lastLoginDttm;
    private String passUpdateDttm;
    private String passUpdateYn;
    private String permSndCancel;
    private String updateDttm;
    private String permNom;
    private String permNomAgree;
    private String permMms;
    private String permMmsAgree;
    private String mfaYn;
}
