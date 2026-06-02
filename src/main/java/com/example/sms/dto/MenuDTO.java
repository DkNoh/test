package com.example.sms.dto;

import lombok.Data;
import java.util.List;

/**
 * [메뉴 데이터 전송 객체]
 * TB_MENU 테이블과 매핑되며, 프론트엔드(사이드바) 렌더링 시 계층형 데이터 구조를 지원합니다.
 */
@Data
public class MenuDTO {
    private String menuCd;     // 메뉴코드
    private String menuNm;     // 메뉴명
    private String menuUrl;    // 접속 URL
    private String upMenuCd;   // 상위메뉴코드
    private Integer sortOrd;   // 정렬순서
    private String useYn;      // 사용여부
    
    // UI 렌더링용 자식 메뉴 리스트 (1 Depth -> 2 Depth 구조 트리 매핑용)
    private List<MenuDTO> subMenus; 
}
