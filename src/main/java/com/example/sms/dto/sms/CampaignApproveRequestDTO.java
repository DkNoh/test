package com.example.sms.dto.sms;

import lombok.Data;

@Data
public class CampaignApproveRequestDTO {
    private Long   campaignId;
    private String action;     // APPROVED / REJECTED
}
