package com.example.sms.dto;

import com.example.sms.dto.common.PageRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApprovalSearchRequestDTO extends PageRequestDTO {
    private String aprvStatus;
    private String reqUserId;
    private String startDate;
    private String endDate;
}
