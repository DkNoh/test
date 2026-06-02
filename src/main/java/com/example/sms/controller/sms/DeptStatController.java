package com.example.sms.controller.sms;

import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.sms.DeptStatSearchRequestDTO;
import com.example.sms.dto.sms.DeptStatVO;
import com.example.sms.service.sms.DeptStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sms/dept-stat")
@RequiredArgsConstructor
public class DeptStatController {

    private final DeptStatService service;

    @GetMapping
    public String page() {
        return "sms/dept-stat-manage";
    }

    @ResponseBody
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<PageResponseDTO<DeptStatVO>>> getData(@ModelAttribute DeptStatSearchRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(service.search(request)));
    }

    @ResponseBody
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<String>> save(@RequestBody DeptStatVO vo) {
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
    public void downloadExcel(@ModelAttribute DeptStatSearchRequestDTO request, jakarta.servlet.http.HttpServletResponse response) {
        service.downloadExcel(request, response);
    }
}
