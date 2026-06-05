package com.example.sms.controller.basic;

import com.example.sms.dto.basic.MessageSearchRequestDTO;
import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.service.basic.MessageService;
import com.example.sms.vo.basic.MessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/basic/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService service;

    @GetMapping
    public String page() {
        return "basic/message-manage";
    }

    @ResponseBody
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<PageResponseDTO<MessageVO>>> getData(
            @ModelAttribute MessageSearchRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(service.search(request)));
    }
}
