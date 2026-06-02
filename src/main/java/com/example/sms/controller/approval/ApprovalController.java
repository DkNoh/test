package com.example.sms.controller.approval;

import com.example.sms.dto.ApprovalDTO;
import com.example.sms.service.approval.ApprovalService;
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
import com.example.sms.dto.ApprovalSearchRequestDTO;

/**
 * [승인 관리 컨트롤러]
 * 화면 렌더링 및 화면 내 자체 비동기 데이터(TUI Grid)를 통합하여 서비스합니다. (Two-Track 통합)
 */
@Slf4j
@Controller
@RequestMapping("/approval")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    /**
     * [Track 1] 결재 화면 렌더링 (View)
     * URL: GET /approval
     */
    @GetMapping
    public String approvalList() {
        return "approval/approval-manage";
    }

    /**
     * [Track 2] TUI Grid용 데이터 서빙 (비동기 JSON 응답)
     * URL: GET /approval/data
     */
    @ResponseBody
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<PageResponseDTO<ApprovalDTO>>> getApprovalList(@ModelAttribute ApprovalSearchRequestDTO request) {
        PageResponseDTO<ApprovalDTO> pageData = approvalService.getApprovalList(request);
        return ResponseEntity.ok(ApiResponse.success(pageData));
    }

    @ResponseBody
    @GetMapping("/test-insert")
    public String insertDummyData() {
        String[] types = {"SMS", "LMS", "ALIMTALK"};
        String[] statuses = {"PENDING", "APPROVED", "REJECTED"};
        String[] users = {"user01", "user02", "admin"};
        
        int count = 0;
        for (int i = 1; i <= 25; i++) {
            ApprovalDTO dto = ApprovalDTO.builder()
                .aprvId("APR-TEST-" + System.currentTimeMillis() + "-" + i)
                .aprvType(types[i % 3])
                .aprvStatus(statuses[i % 3])
                .refTable("TB_CAMPAIGN")
                .refPk("CAMP-" + i)
                .reqTitle("테스트 캠페인 대량발송 요청 " + i)
                .reqUserId(users[i % 3])
                .aprvUserId("admin")
                .build();
            try {
                approvalService.createApproval(dto);
                count++;
            } catch (Exception e) {}
        }
        return "Inserted " + count + " dummy records!";
    }

    /**
     * [Track 2] 일괄 승인/반려 처리 (비동기 JSON 응답)
     * URL: POST /approval/process
     */
    @ResponseBody
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processApproval(@RequestBody List<ApprovalDTO> dtoList) {
        int processedCount = 0;
        for (ApprovalDTO dto : dtoList) {
            processedCount += approvalService.processApproval(dto);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("result", true);
        response.put("processedCount", processedCount);
        response.put("message", processedCount + "건이 성공적으로 처리되었습니다.");
        
        return ResponseEntity.ok(response);
    }
}
