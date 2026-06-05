package com.example.sms.controller.sms;

import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageRequestDTO;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.service.sms.CampaignTargetService;
import com.example.sms.vo.sms.CampaignTargetVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/campaign")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignTargetService service;

    @GetMapping("/target-manage")
    public String targetManagePage() {
        return "campaign/target-manage";
    }

    @ResponseBody
    @GetMapping("/target-manage/data")
    public ResponseEntity<ApiResponse<PageResponseDTO<CampaignTargetVO>>> getData(
            @ModelAttribute PageRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(service.search(request)));
    }
}
