package com.example.sms.vo.system;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EmployeeVO {
    private String empId;
    private String empName;      // EMP_NAME
    private String deptId;       // DEPT_ID (FK → TB_DEPT)
    private String deptNm;       // TB_DEPT.DEPT_NM (JOIN)
    private String useYn;        // USE_YN
    private String userRole;     // USER_ROLE (ROLE_ADMIN / ROLE_USER)
    private String authRead;     // AUTH_READ
    private String authApprove;  // AUTH_APPROVE
    private String authCampaign; // AUTH_CAMPAIGN
    private String lastLoginIp;  // LAST_LOGIN_IP
    private LocalDateTime lastLoginTime; // LAST_LOGIN_TIME
    private LocalDateTime createdAt;     // CREATED_AT
    private LocalDateTime updatedAt;     // UPDATED_AT
    private String        mfaYn;         // MFA_YN
}
