package com.example.sms.controller.sms;

import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.sms.DeptStatSearchRequestDTO;
import com.example.sms.service.sms.DeptStatService;
import com.example.sms.vo.sms.DeptStatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/sms/dept-stat")
@RequiredArgsConstructor
public class DeptStatController {

    private final DeptStatService service;

    @GetMapping
    public String page() {
        return "sms/dept-stat-manage";
    }

    /** TuiPageBuilder 호환 (기존 유지) */
    @ResponseBody
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<PageResponseDTO<DeptStatVO>>> getData(
            @ModelAttribute DeptStatSearchRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(service.search(request)));
    }

    /** 차트용 — 페이징 없이 전체 시간대 리스트 반환 */
    @ResponseBody
    @GetMapping("/chart")
    public ResponseEntity<ApiResponse<List<DeptStatVO>>> getChartData(
            @RequestParam(required = false) String searchDate) {
        return ResponseEntity.ok(ApiResponse.success(service.getAll(searchDate)));
    }
}
