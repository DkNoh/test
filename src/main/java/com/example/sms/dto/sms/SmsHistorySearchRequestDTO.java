package com.example.sms.dto.sms;

import com.example.sms.dto.common.PageRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SmsHistorySearchRequestDTO extends PageRequestDTO {
    private String startDateTime;
    private String endDateTime;
    private String sendStatus;
    private String receiverNo;
    private String sendType;
}
