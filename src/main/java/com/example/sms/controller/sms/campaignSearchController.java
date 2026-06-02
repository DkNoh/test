package com.example.sms.controller.sms;

import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.sms.campaignSearchSearchRequestDTO;
import com.example.sms.vo.sms.CampaignSearchVO;
import com.example.sms.service.sms.campaignSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sms/campaign")
@RequiredArgsConstructor
public class campaignSearchController {

    private final campaignSearchService service;

    @GetMapping
    public String page() {
        return "sms/campaign-manage";
    }

    @ResponseBody
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<PageResponseDTO<CampaignSearchVO>>> getData(@ModelAttribute campaignSearchSearchRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(service.search(request)));
    }
}
