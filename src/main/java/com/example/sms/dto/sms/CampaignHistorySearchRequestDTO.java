package com.example.sms.dto.sms;

import com.example.sms.dto.common.PageRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CampaignHistorySearchRequestDTO extends PageRequestDTO {
    private String startDate;    // 조회기간 FROM (yyyy-MM-dd)
    private String endDate;      // 조회기간 TO
    private String campaignName; // 캠페인명
    private String regId;        // 등록ID
    private String sendType;     // SMS / LMS / ALIMTALK (컨트롤러에서 고정 주입)
}
