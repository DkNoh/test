package com.example.sms.dto.basic;

import com.example.sms.dto.common.PageRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MessageSearchRequestDTO extends PageRequestDTO {
    // 조회조건 없음 — 페이징만 사용
}
