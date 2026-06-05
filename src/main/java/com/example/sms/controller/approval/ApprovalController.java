package com.example.sms.controller.approval;

import com.example.sms.dto.ApprovalDTO;
import com.example.sms.dto.ApprovalSearchRequestDTO;
import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.service.approval.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
