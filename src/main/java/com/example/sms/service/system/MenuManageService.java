package com.example.sms.service.system;

import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.system.MenuSaveRequestDTO;
import com.example.sms.dto.system.MenuSearchRequestDTO;
import com.example.sms.dto.system.MenuVO;
import com.example.sms.mapper.system.MenuManageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuManageService {

    private final MenuManageMapper menuManageMapper;
    // 스프링 시큐리티 인가 권한 캐시를 비우기 위해 필요하다면 MenuService를 주입받아도 됩니다.
    private final MenuService menuService;

    @Transactional(readOnly = true)
    public PageResponseDTO<MenuVO> searchMenus(MenuSearchRequestDTO request) {
        int total = menuManageMapper.countMenus(request);
        List<MenuVO> list = menuManageMapper.selectMenus(request);
        return PageResponseDTO.of(list, request, total);
    }

    /**
     * [트리형 메뉴 데이터 구성]
     * DB에서 1차원(Flat)으로 가져온 전체 메뉴 리스트를 TUI Grid가 인식할 수 있는
     * 부모-자식(Parent-Child) 계층형 Tree 구조로 재조립하여 반환합니다.
     * 
     * @return 계층형으로 조립된 최상위 메뉴(Root) 리스트
     */
    @Transactional(readOnly = true)
    public List<com.example.sms.dto.system.MenuTreeVO> getMenuTree() {
        List<MenuVO> flatMenus = menuManageMapper.selectAllMenus();
        java.util.Map<String, com.example.sms.dto.system.MenuTreeVO> map = new java.util.LinkedHashMap<>();
        List<com.example.sms.dto.system.MenuTreeVO> rootNodes = new java.util.ArrayList<>();

        // 1. 모든 메뉴를 TreeVO로 변환하여 Map에 임시 저장 (키: 메뉴코드)
        for (MenuVO vo : flatMenus) {
            com.example.sms.dto.system.MenuTreeVO treeVO = new com.example.sms.dto.system.MenuTreeVO();
            org.springframework.beans.BeanUtils.copyProperties(vo, treeVO);
            map.put(treeVO.getMenuCd(), treeVO);
        }

        // 2. 부모-자식 관계 맵핑 (Map을 순회하며 자신의 부모 객체에 자신을 삽입)
        for (com.example.sms.dto.system.MenuTreeVO vo : map.values()) {
            if (vo.getUpMenuCd() == null || vo.getUpMenuCd().trim().isEmpty() || !map.containsKey(vo.getUpMenuCd())) {
                // 부모가 없으면 최상위 루트 노드
                rootNodes.add(vo);
            } else {
                // 부모가 있으면 부모의 _children 리스트에 자신을 추가
                com.example.sms.dto.system.MenuTreeVO parent = map.get(vo.getUpMenuCd());
                // 자식이 최초로 추가될 때만 리스트 초기화 (빈 리스트면 TUI Grid가 폴더로 인식하는 버그 방지)
                if (parent.get_children() == null) {
                    parent.set_children(new java.util.ArrayList<>());
                }
                parent.get_children().add(vo);
            }
        }

        return rootNodes;
    }


    @Transactional(readOnly = true)
    public List<String> getMenuAuths(String menuCd) {
        return menuManageMapper.selectMenuAuths(menuCd);
    }

    @Transactional
    public void saveMenu(MenuSaveRequestDTO request, String loginUserId) {
        MenuVO menu = request.getMenu();
        
        // 정렬 순서 밀어내기 (동일한 부모, 현재 입력한 정렬 순서보다 크거나 같은 것들 + 1)
        menuManageMapper.shiftSortOrder(menu.getUpMenuCd(), menu.getSortOrd(), menu.getMenuCd());
        
        if ("1".equals(request.getMode())) { // 신규
            menu.setRegId(loginUserId);
            menuManageMapper.insertMenu(menu);
        } else { // 수정
            menuManageMapper.updateMenu(menu);
        }

        // 권한 정보 갱신 (기존 권한 날리고 새로 Insert)
        menuManageMapper.deleteMenuAuths(menu.getMenuCd());
        if (request.getAuthRoles() != null) {
            for (String role : request.getAuthRoles()) {
                menuManageMapper.insertMenuAuth(menu.getMenuCd(), role, loginUserId);
            }
        }
    }

    @Transactional
    public void deleteMenu(String menuCd) {
        menuManageMapper.deleteMenuAuths(menuCd);
        menuManageMapper.deleteMenu(menuCd);
    }
}
