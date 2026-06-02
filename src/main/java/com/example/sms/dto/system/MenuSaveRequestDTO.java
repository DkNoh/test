package com.example.sms.dto.system;

import lombok.Data;
import java.util.List;
import com.example.sms.vo.system.MenuVO;

@Data
public class MenuSaveRequestDTO {
    // 1: 신규생성 (기존 메뉴 없음), 2: 수정 (기존 메뉴 덮어쓰기)
    private String mode; 
    
    private MenuVO menu;
    private List<String> authRoles; // 예: ["ROLE_ADMIN", "ROLE_USER"]
}
