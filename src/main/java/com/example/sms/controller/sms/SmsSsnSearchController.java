package com.example.sms.controller.sms;

import com.example.sms.annotation.PrivacyLog;
import com.example.sms.dto.common.ApiResponse;
import com.example.sms.mapper.sms.SmsCustomerSsnMapper;
import com.example.sms.vo.sms.SmsCustomerSsnVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/sms/ssn")
@RequiredArgsConstructor
public class SmsSsnSearchController {

    private final SmsCustomerSsnMapper mapper;

    @GetMapping
    public String page() { return "sms/ssn-search"; }

    @PrivacyLog(action = "주민번호 조회 (식별자 포함)")
    @ResponseBody
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<List<SmsCustomerSsnVO>>> getData(
            @RequestParam(required = false) String receiverNo) {
        return ResponseEntity.ok(ApiResponse.success(mapper.selectList(receiverNo)));
    }
}
