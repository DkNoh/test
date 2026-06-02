package com.example.sms.dto.system;

import lombok.Data;

@Data
public class MenuVO {
    private String menuCd;
    private String menuNm;
    private String menuUrl;
    private String upMenuCd;
    private Integer sortOrd;
    private String regId;
    private String useYn;
    
    // UI 표시용 (하위 권한 목록 문자열)
    private String authNames;
}
