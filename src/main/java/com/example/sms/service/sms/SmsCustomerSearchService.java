package com.example.sms.service.sms;

import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.sms.SmsCustomerSearchRequestDTO;
import com.example.sms.mapper.sms.SmsCustomerSearchMapper;
import com.example.sms.vo.sms.SmsCustomerSearchVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SmsCustomerSearchService {

    private final SmsCustomerSearchMapper mapper;

    @Transactional(readOnly = true)
    public PageResponseDTO<SmsCustomerSearchVO> search(SmsCustomerSearchRequestDTO request) {
        return PageResponseDTO.of(mapper.selectList(request), request, mapper.count(request));
    }
}
