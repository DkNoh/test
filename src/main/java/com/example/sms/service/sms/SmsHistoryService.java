package com.example.sms.service.sms;

import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.sms.SmsHistorySearchRequestDTO;
import com.example.sms.mapper.sms.SmsHistoryMapper;
import com.example.sms.vo.sms.SmsHistoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SmsHistoryService {

    private final SmsHistoryMapper mapper;

    @Transactional(readOnly = true)
    public PageResponseDTO<SmsHistoryVO> search(SmsHistorySearchRequestDTO request) {
        int totalCount = mapper.count(request);
        List<SmsHistoryVO> list = mapper.selectList(request);
        return PageResponseDTO.of(list, request, totalCount);
    }

    @Transactional
    public void save(SmsHistoryVO vo) { mapper.insert(vo); }

    @Transactional
    public void delete(Long id) { mapper.delete(id); }
}
