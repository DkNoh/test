package com.example.sms.dto.basic;

import com.example.sms.dto.common.PageRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CurrencySearchRequestDTO extends PageRequestDTO {
    private String baseDt;     // 기준일자
    private String currencyCd; // 통화코드 (선택)
    private String providerCd; // 제공기관코드 (선택)
    private String bankCd;     // 은행코드 (콤보 또는 텍스트 자동완성)
}
