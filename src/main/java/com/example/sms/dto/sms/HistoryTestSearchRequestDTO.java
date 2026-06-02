package com.example.sms.dto.sms;

import com.example.sms.dto.common.PageRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HistoryTestSearchRequestDTO extends PageRequestDTO {
    private String startDate;
    private String endDate;
    private String receiverNo;
    private String sendType;
}
