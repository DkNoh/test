package com.example.sms.dto.dashboard;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardResponseDTO {
    private KpiDTO kpi;
    private List<WeeklyTrendDTO> weeklyTrend;
    private List<ChannelRatioDTO> channelRatio;
    private List<FailLogDTO> recentFailLogs;

    @Data
    public static class KpiDTO {
        private long totalSendCount;
        private double successRate;
        private long failCount;
        // NOTE: 원칙에 따라 KRW 금액은 절대 double을 쓰지 않고 BigDecimal 사용
        private BigDecimal estimatedCost; 
    }

    @Data
    public static class WeeklyTrendDTO {
        private String dayOfWeek;
        private long sendCount;
        private int percentage;
    }

    @Data
    public static class ChannelRatioDTO {
        private String channelName;
        private double ratio;
        private String colorClass;
    }

    @Data
    public static class FailLogDTO {
        private String failDate;
        private String sendType;
        private String receiverNo;
        private String errorCode;
        private String failReason;
    }
}
