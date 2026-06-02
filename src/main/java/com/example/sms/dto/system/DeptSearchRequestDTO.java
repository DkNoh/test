package com.example.sms.dto.system;

import com.example.sms.dto.common.PageRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeptSearchRequestDTO extends PageRequestDTO {
    private String depId;
    private String depNm;
}
