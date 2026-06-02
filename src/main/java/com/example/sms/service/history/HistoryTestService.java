package com.example.sms.service.history;

import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.sms.HistoryTestSearchRequestDTO;
import com.example.sms.dto.sms.HistoryTestVO;
import com.example.sms.mapper.sms.HistoryTestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryTestService {

    private final HistoryTestMapper mapper;

    @Transactional(readOnly = true)
    public PageResponseDTO<HistoryTestVO> search(HistoryTestSearchRequestDTO request) {
        int totalCount = mapper.count(request);
        List<HistoryTestVO> list = mapper.selectList(request);
        return PageResponseDTO.of(list, request, totalCount);
    }
}
