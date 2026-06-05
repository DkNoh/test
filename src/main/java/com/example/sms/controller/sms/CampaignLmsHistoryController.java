package com.example.sms.controller.sms;

import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.sms.CampaignHistorySearchRequestDTO;
import com.example.sms.service.sms.CampaignHistoryService;
import com.example.sms.vo.sms.CampaignHistoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sms/campaign-lms")
@RequiredArgsConstructor
public class CampaignLmsHistoryController {

    private final CampaignHistoryService service;

    @GetMapping
    public String page() { return "sms/campaign-lms-manage"; }

    @ResponseBody
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<PageResponseDTO<CampaignHistoryVO>>> getData(
            @ModelAttribute CampaignHistorySearchRequestDTO request) {
        request.setSendType("LMS");
        return ResponseEntity.ok(ApiResponse.success(service.search(request)));
    }
}
