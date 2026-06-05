package com.example.sms.controller.basic;

import com.example.sms.dto.basic.MfaUserSearchRequestDTO;
import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.service.basic.MfaUserService;
import com.example.sms.vo.basic.MfaUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/basic/mfa")
@RequiredArgsConstructor
public class MfaUserController {

    private final MfaUserService service;

    @GetMapping
    public String page() {
        return "basic/mfa-manage";
    }

    @ResponseBody
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<PageResponseDTO<MfaUserVO>>> getData(
            @ModelAttribute MfaUserSearchRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(service.search(request)));
    }
}
