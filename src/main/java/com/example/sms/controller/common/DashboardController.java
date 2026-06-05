package com.example.sms.controller.common;

import com.example.sms.dto.dashboard.DashboardResponseDTO;
import com.example.sms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardResponseDTO> getSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }
}
