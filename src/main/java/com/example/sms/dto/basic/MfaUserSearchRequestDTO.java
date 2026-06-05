package com.example.sms.dto.basic;

import com.example.sms.dto.common.PageRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MfaUserSearchRequestDTO extends PageRequestDTO {
    private String userGroupCode; // 사용자그룹명 콤보 → USER_ROLE 값으로 필터
    private String userRole;      // 레벨 콤보 → USER_ROLE
    private String empName;       // 사용자명 (텍스트 검색)
    private String empId;         // 사용자ID (텍스트 검색)
}
