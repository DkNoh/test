package com.example.sms.controller.system;

import com.example.sms.dto.AuthorityDTO;
import com.example.sms.service.system.AuthorityService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.AuthoritySearchRequestDTO;

/**
 * [권한 관리 컨트롤러]
 * 화면 렌더링 및 비동기 데이터를 단일 책임으로 통합 서비스합니다. (Two-Track 전략)
 */
@Slf4j
@Controller
@RequestMapping("/authority")
@RequiredArgsConstructor
public class AuthorityController {

    private final AuthorityService authorityService;

    /**
     * [Track 1] 권한 관리 메인 화면 (View)
     */
    @GetMapping("/manage")
    public String authorityManage() {
        return "authority/authority-manage";
    }

    /**
     * [Track 2] TUI Grid용 권한 목록 서빙 (JSON)
     */
    @ResponseBody
    @GetMapping("/manage/data")
    public ResponseEntity<ApiResponse<PageResponseDTO<AuthorityDTO>>> getAuthorityList(@ModelAttribute AuthoritySearchRequestDTO request) {
        PageResponseDTO<AuthorityDTO> pageData = authorityService.getAuthorityList(request);
        return ResponseEntity.ok(ApiResponse.success(pageData));
    }

    /**
     * [Track 2] 신규 권한 등록 (JSON)
     */
    @ResponseBody
    @PostMapping("/manage/create")
    public ResponseEntity<Map<String, Object>> createAuthority(@RequestBody AuthorityDTO dto) {
        // NOTE: 실제 운영에서는 Spring Security Context에서 사번을 추출합니다.
        dto.setRegId("SYSTEM"); 
        
        int resultCount = authorityService.createAuthority(dto);
        
        Map<String, Object> response = new HashMap<>();
        response.put("result", resultCount > 0);
        return ResponseEntity.ok(response);
    }

    /**
     * [Track 2] 선택 권한 일괄 삭제 (JSON)
     */
    @ResponseBody
    @PostMapping("/manage/delete")
    public ResponseEntity<Map<String, Object>> deleteAuthorities(@RequestBody List<String> authCdList) {
        int deletedCount = 0;
        for (String authCd : authCdList) {
            deletedCount += authorityService.deleteAuthority(authCd);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("result", true);
        response.put("deletedCount", deletedCount);
        return ResponseEntity.ok(response);
    }
}
