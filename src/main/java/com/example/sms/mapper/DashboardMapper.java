package com.example.sms.mapper;

import com.example.sms.dto.dashboard.DashboardResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DashboardMapper {
    DashboardResponseDTO.KpiDTO selectKpiSummary();
    List<DashboardResponseDTO.WeeklyTrendDTO> selectWeeklyTrend();
    List<DashboardResponseDTO.ChannelRatioDTO> selectChannelRatio();
    List<DashboardResponseDTO.FailLogDTO> selectRecentFailLogs();
}
