package com.example.sms.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PrivacyAuditLogVO {
    private Long logId;
    private String executorId;
    private String executorIp;
    private String requestUrl;
    private String actionType;
    private String targetData;
    private LocalDateTime createdAt;
}