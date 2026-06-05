package com.example.sms.controller.api;

import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.sms.SmsHistorySearchRequestDTO;
import com.example.sms.dto.sms.SmsHistorySendResultDTO;
import com.example.sms.service.sms.SmsHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sms/history")
@RequiredArgsConstructor
public class SmsHistoryApiController {

    private final SmsHistoryService smsHistoryService;

    @GetMapping("/send-result")
    public ResponseEntity<ApiResponse<PageResponseDTO<SmsHistorySendResultDTO>>> getSendResults(
            @ModelAttribute SmsHistorySearchRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(smsHistoryService.searchSendResults(request)));
    }
}
