package com.example.sms.controller.system;

import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.system.MenuSaveRequestDTO;
import com.example.sms.dto.system.MenuSearchRequestDTO;
import com.example.sms.vo.system.MenuVO;
import com.example.sms.service.system.MenuManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/system/menu-manage")
@RequiredArgsConstructor
public class MenuManageController {

    private final MenuManageService service;
    private final com.example.sms.service.system.MenuService menuService;

    @GetMapping
    public String page() {
        return "system/menu-manage";
    }

    // [Tree Prototype] 트리 뷰 화면
    @GetMapping("/tree")
    public String treePage() {
        return "system/menu-tree";
    }

    // [Tree Prototype] 트리용 전체 데이터 조회
    @ResponseBody
    @GetMapping("/tree-data")
    public ResponseEntity<ApiResponse<List<com.example.sms.dto.system.MenuTreeVO>>> getTreeData() {
        return ResponseEntity.ok(ApiResponse.success(service.getMenuTree()));
    }


    @ResponseBody
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<PageResponseDTO<MenuVO>>> getData(@ModelAttribute MenuSearchRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(service.searchMenus(request)));
    }

    @ResponseBody
    @GetMapping("/auth-data")
    public ResponseEntity<ApiResponse<List<String>>> getAuthData(@RequestParam String menuCd) {
        return ResponseEntity.ok(ApiResponse.success(service.getMenuAuths(menuCd)));
    }

    @ResponseBody
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<String>> save(@RequestBody MenuSaveRequestDTO request,
                                                    jakarta.servlet.http.HttpSession session,
                                                    org.springframework.security.core.Authentication auth) {
        // 실제로는 Session에서 로그인 ID를 꺼내와야 합니다. 현재는 강제로 "SYSTEM" 주입
        service.saveMenu(request, "SYSTEM");
        
        // 현재 접속된 사용자의 좌측 사이드바 메뉴 갱신
        if (auth != null && auth.getAuthorities().iterator().hasNext()) {
            String role = auth.getAuthorities().iterator().next().getAuthority();
            session.setAttribute("userMenus", menuService.getHierarchicalMenus(role));
        }
        
        return ResponseEntity.ok(ApiResponse.success("정상적으로 저장되었습니다."));
    }

    @ResponseBody
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<String>> delete(@RequestParam String menuCd,
                                                      jakarta.servlet.http.HttpSession session,
                                                      org.springframework.security.core.Authentication auth) {
        service.deleteMenu(menuCd);
        
        // 현재 접속된 사용자의 좌측 사이드바 메뉴 갱신
        if (auth != null && auth.getAuthorities().iterator().hasNext()) {
            String role = auth.getAuthorities().iterator().next().getAuthority();
            session.setAttribute("userMenus", menuService.getHierarchicalMenus(role));
        }
        
        return ResponseEntity.ok(ApiResponse.success("삭제되었습니다."));
    }
}
