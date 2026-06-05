package com.example.sms.dto.sms;

import com.example.sms.dto.common.PageRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeptStatSearchRequestDTO extends PageRequestDTO {
    private String searchDate; // 조회일자 (yyyy-MM-dd) 단일 날짜
}
