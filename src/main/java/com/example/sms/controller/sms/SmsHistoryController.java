package com.example.sms.controller.sms;

import com.example.sms.annotation.PrivacyLog;
import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.sms.SmsHistorySearchRequestDTO;
import com.example.sms.service.sms.SmsHistoryService;
import com.example.sms.vo.sms.SmsHistoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sms/history")
@RequiredArgsConstructor
public class SmsHistoryController {

    private final SmsHistoryService service;

    @GetMapping
    public String page() { return "sms/history-manage"; }

    @PrivacyLog(action = "SMS 발송내역 조회 (수신번호·메세지 포함)")
    @ResponseBody
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<PageResponseDTO<SmsHistoryVO>>> getData(
            @ModelAttribute SmsHistorySearchRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(service.search(request)));
    }

    @ResponseBody
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<String>> save(@RequestBody SmsHistoryVO vo) {
        service.save(vo);
        return ResponseEntity.ok(ApiResponse.success("저장되었습니다."));
    }

    @ResponseBody
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<String>> delete(@RequestParam Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("삭제되었습니다."));
    }
}
