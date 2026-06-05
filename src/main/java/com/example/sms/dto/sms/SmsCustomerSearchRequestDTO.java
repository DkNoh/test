package com.example.sms.dto.sms;

import com.example.sms.dto.common.PageRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SmsCustomerSearchRequestDTO extends PageRequestDTO {
    private String msgType;
    private String receiverNo;
    private String searchDate;
}
