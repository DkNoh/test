package com.example.sms.controller.approval;

import com.example.sms.dto.ApprovalDTO;
import com.example.sms.dto.ApprovalSearchRequestDTO;
import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.service.approval.ApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/approval")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    @GetMapping
    public String approvalList() {
        return "approval/approval-manage";
    }

    @ResponseBody
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<PageResponseDTO<ApprovalDTO>>> getApprovalList(
            @ModelAttribute ApprovalSearchRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(approvalService.getApprovalList(request)));
    }

    @ResponseBody
    @GetMapping("/test-insert")
    public String insertDummyData() {
        String[] types    = {"SMS", "LMS", "ALIMTALK"};
        String[] statuses = {"PENDING", "APPROVED", "REJECTED"};
        String[] users    = {"user01", "user02", "admin"};

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

    @ResponseBody
    @PostMapping("/process")
    public ResponseEntity<ApiResponse<Integer>> processApproval(
            @RequestBody List<ApprovalDTO> dtoList) {
        int processedCount = 0;
        for (ApprovalDTO dto : dtoList) {
            processedCount += approvalService.processApproval(dto);
        }
        return ResponseEntity.ok(
            ApiResponse.success(processedCount + "건이 성공적으로 처리되었습니다.", processedCount));
    }
}
