package com.example.sms.service.basic;

import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.basic.CurrencySearchRequestDTO;
import com.example.sms.vo.basic.CurrencyVO;
import com.example.sms.mapper.basic.CurrencyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyMapper mapper;

    @Transactional(readOnly = true)
    public PageResponseDTO<CurrencyVO> search(CurrencySearchRequestDTO request) {
        int totalCount = mapper.count(request);
        List<CurrencyVO> list = mapper.selectList(request);
        return PageResponseDTO.of(list, request, totalCount);
    }

    @Transactional
    public void save(CurrencyVO vo) {
        // TODO: 신규/수정 분기 로직 구현
        mapper.insert(vo);
    }

    @Transactional
    public void delete(String id) {
        mapper.delete(id);
    }

    @Transactional(readOnly = true)
    public void downloadExcel(CurrencySearchRequestDTO request, jakarta.servlet.http.HttpServletResponse response) {
        String[] headers = {"RATE_SEQ", "BASE_DT", "CURRENCY_CD", "CURRENCY_NM", "COUNTRY_NM", "UNIT", "PROVIDER_CD", "PROVIDER_NM", "BASE_RATE", "CASH_BUY_RATE", "CASH_SELL_RATE", "SEND_RATE", "RECV_RATE"};
        String[] keys = {"RATE_SEQ", "BASE_DT", "CURRENCY_CD", "CURRENCY_NM", "COUNTRY_NM", "UNIT", "PROVIDER_CD", "PROVIDER_NM", "BASE_RATE", "CASH_BUY_RATE", "CASH_SELL_RATE", "SEND_RATE", "RECV_RATE"};
        java.util.List<java.util.Map<String, Object>> list = mapper.selectListForExcel(request);
        com.example.sms.util.ExcelUtil.downloadExcel(response, "Currency_export", headers, list, keys);
    }
}