package com.example.sms.vo.basic;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CurrencyVO {
    private long rowNum;
    private Long rateSeq;
    private String baseDt;
    private String currencyCd;
    private String currencyNm;
    private String countryNm;
    private Integer unit;
    private String providerCd;
    private String providerNm;
    private BigDecimal baseRate;
    private BigDecimal cashBuyRate;
    private BigDecimal cashSellRate;
    private BigDecimal sendRate;
    private BigDecimal recvRate;
}