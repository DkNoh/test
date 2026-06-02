package com.example.sms.controller.api;

import com.example.sms.dto.dashboard.DashboardResponseDTO;
import com.example.sms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardApiController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardResponseDTO> getSummary() {
        // NOTE: 예외처리는 GlobalExceptionHandler에서 일괄 담당
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }
}
