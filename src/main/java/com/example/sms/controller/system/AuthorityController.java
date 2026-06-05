package com.example.sms.controller.system;

import com.example.sms.dto.AuthorityDTO;
import com.example.sms.dto.AuthoritySearchRequestDTO;
import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.service.system.AuthorityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/authority")
@RequiredArgsConstructor
public class AuthorityController {

    private final AuthorityService authorityService;

    @GetMapping("/manage")
    public String authorityManage() {
        return "authority/authority-manage";
    }

    @ResponseBody
    @GetMapping("/manage/data")
    public ResponseEntity<ApiResponse<PageResponseDTO<AuthorityDTO>>> getAuthorityList(
            @ModelAttribute AuthoritySearchRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(authorityService.getAuthorityList(request)));
    }

    @ResponseBody
    @PostMapping("/manage/create")
    public ResponseEntity<ApiResponse<String>> createAuthority(@RequestBody AuthorityDTO dto) {
        dto.setRegId("SYSTEM");
        authorityService.createAuthority(dto);
        return ResponseEntity.ok(ApiResponse.success("등록되었습니다."));
    }

    @ResponseBody
    @PostMapping("/manage/delete")
    public ResponseEntity<ApiResponse<Integer>> deleteAuthorities(
            @RequestBody List<String> authCdList) {
        int deletedCount = 0;
        for (String authCd : authCdList) {
            deletedCount += authorityService.deleteAuthority(authCd);
        }
        return ResponseEntity.ok(
            ApiResponse.success(deletedCount + "건이 삭제되었습니다.", deletedCount));
    }
}
