package com.example.sms.dto.system;

import com.example.sms.dto.common.PageRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SystemMessageSearchRequestDTO extends PageRequestDTO {
    private String msgType;  // 메세지유형 (NORMAL/KAKAO/AD) — 컨트롤러에서 고정 주입 가능
    private String deptId;   // 부서코드
    private String msgCode;  // 메세지코드
    private String deptNm;   // 부서명
    private String msgTitle; // 메세지제목
}
