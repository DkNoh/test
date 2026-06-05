package com.example.sms.controller.system;

import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.system.SystemMessageSearchRequestDTO;
import com.example.sms.service.system.SystemMessageService;
import com.example.sms.vo.system.SystemMessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/system/message-manage")
@RequiredArgsConstructor
public class SystemMessageController {

    private final SystemMessageService service;

    @GetMapping
    public String page() { return "system/message-manage-page"; }

    @ResponseBody
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<PageResponseDTO<SystemMessageVO>>> getData(
            @ModelAttribute SystemMessageSearchRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(service.search(request)));
    }
}
