package com.example.sms.dto.sms;

import com.example.sms.dto.common.PageRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeptSendStatSearchRequestDTO extends PageRequestDTO {
    private String searchDate;
    private String deptId;
}
