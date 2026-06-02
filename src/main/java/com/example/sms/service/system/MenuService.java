package com.example.sms.service.system;

import com.example.sms.dto.MenuDTO;
import com.example.sms.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * [동적 화면 접근 제어 비즈니스 서비스]
 * 접속 권한에 따른 메뉴 구조 생성 및 URL 접근 인가 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuMapper menuMapper;

    /**
     * 권한 코드에 매핑된 전체 메뉴를 조회하여 UI 렌더링용 계층형(1 Depth -> 2 Depth) 트리 구조로 조립합니다.
     * 
     * @param authCd 사용자의 권한 (예: ROLE_ADMIN)
     * @return 2Depth 구조가 세팅된 최상위 부모 메뉴 리스트
     */
    public List<MenuDTO> getHierarchicalMenus(String authCd) {
        // [보안/예외 방어] 권한 코드가 null 이거나 비어있으면 DB 조회를 생략하고 빈 리스트를 반환합니다.
        // (Oracle JDBC '부적합한 열 유형: 1111' 에러 방지)
        if (authCd == null || authCd.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        // 1. 권한에 해당하는 모든 활성 메뉴를 Flat하게 퍼옴
        List<MenuDTO> flatMenus = menuMapper.selectMenusByAuthCode(authCd);
        
        // 2. 1Depth(최상위 부모) 메뉴만 필터링 (상위 메뉴 코드가 없는 것)
        List<MenuDTO> parentMenus = flatMenus.stream()
                .filter(m -> m.getUpMenuCd() == null || m.getUpMenuCd().isEmpty())
                .collect(Collectors.toList());

        // 3. 각 부모 메뉴 객체 안에 해당 부모를 바라보는 자식 메뉴들을 매핑
        for (MenuDTO parent : parentMenus) {
            List<MenuDTO> children = flatMenus.stream()
                    .filter(m -> parent.getMenuCd().equals(m.getUpMenuCd()))
                    .collect(Collectors.toList());
            parent.setSubMenus(children);
        }

        return parentMenus;
    }
    
    /**
     * 특정 URL에 사용자가 접근할 권한이 있는지 DB와 실시간 검증합니다.
     * 
     * @param menuUrl 사용자가 주소창에 친 URL 경로
     * @param authCd 사용자의 권한 코드
     * @return 권한 유무 (true/false)
     */
    public boolean hasAccess(String menuUrl, String authCd) {
        return menuMapper.countAuthForUrl(menuUrl, authCd) > 0;
    }
}
