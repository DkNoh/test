package com.example.sms.controller.basic;

import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.basic.CurrencySearchRequestDTO;
import com.example.sms.vo.basic.CurrencyVO;
import com.example.sms.service.basic.CurrencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/basic/currency")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService service;

    @GetMapping
    public String page() {
        return "basic/currency-manage";
    }

    @ResponseBody
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<PageResponseDTO<CurrencyVO>>> getData(@ModelAttribute CurrencySearchRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(service.search(request)));
    }

    @ResponseBody
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<String>> save(@Valid @RequestBody CurrencyVO vo) {
        service.save(vo);
        return ResponseEntity.ok(ApiResponse.success("저장되었습니다."));
    }

    @ResponseBody
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<String>> delete(@RequestParam String id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("삭제되었습니다."));
    }

    @GetMapping("/excel")
    public void downloadExcel(@ModelAttribute CurrencySearchRequestDTO request, jakarta.servlet.http.HttpServletResponse response) {
        service.downloadExcel(request, response);
    }
}