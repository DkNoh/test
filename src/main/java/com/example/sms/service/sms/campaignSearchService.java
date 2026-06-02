package com.example.sms.service.sms;

import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.sms.campaignSearchSearchRequestDTO;
import com.example.sms.vo.sms.CampaignSearchVO;
import com.example.sms.mapper.sms.campaignSearchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class campaignSearchService {

    private final campaignSearchMapper mapper;

    @Transactional(readOnly = true)
    public PageResponseDTO<CampaignSearchVO> search(campaignSearchSearchRequestDTO request) {
        int totalCount = mapper.count(request);
        List<CampaignSearchVO> list = mapper.selectList(request);
        return PageResponseDTO.of(list, request, totalCount);
    }
}
