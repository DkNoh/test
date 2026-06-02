package com.example.sms.service;

import com.example.sms.dto.dashboard.DashboardResponseDTO;
import com.example.sms.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardMapper dashboardMapper;

    /**
     * 대시보드 통계 정보를 조립하여 반환합니다.
     * NOTE: 실무 적용 시 @Cacheable(value="dashboardCache", key="'summary'") 를 적용하여 DB 부하 방어 필수.
     */
    @Transactional(readOnly = true) // NOTE: 조회 성능 향상 및 안전성 보장
    public DashboardResponseDTO getDashboardSummary() {
        DashboardResponseDTO response = new DashboardResponseDTO();
        
        // 개별 통계 쿼리 실행 후 조립 패턴 (Assemble)
        response.setKpi(dashboardMapper.selectKpiSummary());
        response.setWeeklyTrend(dashboardMapper.selectWeeklyTrend());
        response.setChannelRatio(dashboardMapper.selectChannelRatio());
        response.setRecentFailLogs(dashboardMapper.selectRecentFailLogs());
        
        return response;
    }
}
