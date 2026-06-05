package com.example.sms.controller.campaign;

import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.sms.CampaignApproveRequestDTO;
import com.example.sms.service.sms.CampaignTargetService;
import com.example.sms.vo.sms.CampaignTargetVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/campaign")
@RequiredArgsConstructor
public class CampaignSendController {

    private final CampaignTargetService service;

    // ── 등록 화면 ──────────────────────────────────────────────
    @GetMapping("/sms/register")
    public String smsRegister()      { return "campaign/sms-register"; }

    @GetMapping("/lms/register")
    public String lmsRegister()      { return "campaign/lms-register"; }

    @GetMapping("/alimtalk/register")
    public String alimtalkRegister() { return "campaign/alimtalk-register"; }

    // ── 승인 화면 ──────────────────────────────────────────────
    @GetMapping("/sms/approve")
    public String smsApprove()       { return "campaign/sms-approve"; }

    @GetMapping("/lms/approve")
    public String lmsApprove()       { return "campaign/lms-approve"; }

    @GetMapping("/alimtalk/approve")
    public String alimtalkApprove()  { return "campaign/alimtalk-approve"; }

    // ── 등록 API (공통) ────────────────────────────────────────
    @ResponseBody
    @PostMapping("/{sendType}/submit")
    public ResponseEntity<ApiResponse<String>> submit(
            @PathVariable String sendType,
            @RequestBody CampaignTargetVO vo) {
        vo.setSendType(sendType.toUpperCase());
        service.register(vo, currentUserId());
        return ResponseEntity.ok(ApiResponse.success("등록되었습니다. 승인을 요청했습니다."));
    }

    // ── 승인 대기 목록 API ──────────────────────────────────────
    @ResponseBody
    @GetMapping("/{sendType}/pending")
    public ResponseEntity<ApiResponse<List<CampaignTargetVO>>> pending(
            @PathVariable String sendType) {
        return ResponseEntity.ok(ApiResponse.success(service.getPending(sendType.toUpperCase())));
    }

    // ── 승인 처리 API ──────────────────────────────────────────
    @ResponseBody
    @PostMapping("/approve")
    public ResponseEntity<ApiResponse<String>> approve(
            @RequestBody CampaignApproveRequestDTO request) {
        if ("REJECTED".equals(request.getAction())) {
            service.reject(request.getCampaignId(), currentUserId());
        } else {
            service.approve(request.getCampaignId(), currentUserId());
        }
        return ResponseEntity.ok(ApiResponse.success("처리되었습니다."));
    }

    private String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SYSTEM";
    }
}
