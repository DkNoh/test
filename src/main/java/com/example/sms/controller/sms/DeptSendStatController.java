package com.example.sms.controller.sms;

import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.sms.DeptSendStatSearchRequestDTO;
import com.example.sms.service.sms.DeptSendStatService;
import com.example.sms.vo.sms.DeptSendStatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/statistics/dept-send-stat")
@RequiredArgsConstructor
public class DeptSendStatController {

    private final DeptSendStatService service;

    @GetMapping
    public String page() {
        return "statistics/dept-send-stat";
    }

    @ResponseBody
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<PageResponseDTO<DeptSendStatVO>>> getData(
            @ModelAttribute DeptSendStatSearchRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(service.search(request)));
    }
}
