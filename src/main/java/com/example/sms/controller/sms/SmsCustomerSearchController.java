package com.example.sms.controller.sms;

import com.example.sms.annotation.PrivacyLog;
import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.sms.SmsCustomerSearchRequestDTO;
import com.example.sms.service.sms.SmsCustomerSearchService;
import com.example.sms.vo.sms.SmsCustomerSearchVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sms/customer")
@RequiredArgsConstructor
public class SmsCustomerSearchController {

    private final SmsCustomerSearchService service;

    @GetMapping
    public String page() { return "sms/customer-search"; }

    @PrivacyLog(action = "고객별 SMS 조회 (수신번호·메세지 포함)")
    @ResponseBody
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<PageResponseDTO<SmsCustomerSearchVO>>> getData(
            @ModelAttribute SmsCustomerSearchRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(service.search(request)));
    }
}
