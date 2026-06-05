package com.example.sms.vo.basic;

import lombok.Data;

@Data
public class MfaUserVO {
    private long   rowNum;
    private String userGroupCode; // 사용자그룹코드 (USER_ROLE)
    private String userGroupNm;   // 사용자그룹명   (TODO: TB_AUTHORITY.AUTH_NM 또는 USER_ROLE 한글명)
    private String empId;         // 사용자ID
    private String empName;       // 사용자명
    private String userRole;      // 레벨 (USER_ROLE)
    private String useYn;         // 사용여부
    private String mfaYn;         // MFA사용여부 (TODO: EMPLOYEE 테이블에 컬럼 없음 — DB 확인 필요)
}
