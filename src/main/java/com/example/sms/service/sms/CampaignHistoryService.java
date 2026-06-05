package com.example.sms.service.sms;

import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.sms.CampaignHistorySearchRequestDTO;
import com.example.sms.mapper.sms.CampaignHistoryMapper;
import com.example.sms.vo.sms.CampaignHistoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignHistoryService {

    private final CampaignHistoryMapper mapper;

    @Transactional(readOnly = true)
    public PageResponseDTO<CampaignHistoryVO> search(CampaignHistorySearchRequestDTO request) {
        int totalCount = mapper.count(request);
        List<CampaignHistoryVO> list = mapper.selectList(request);
        return PageResponseDTO.of(list, request, totalCount);
    }
}
