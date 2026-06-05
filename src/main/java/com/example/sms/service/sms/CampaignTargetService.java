package com.example.sms.service.sms;

import com.example.sms.dto.common.PageRequestDTO;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.mapper.sms.CampaignTargetMapper;
import com.example.sms.vo.sms.CampaignTargetVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignTargetService {

    private final CampaignTargetMapper mapper;

    @Transactional(readOnly = true)
    public PageResponseDTO<CampaignTargetVO> search(PageRequestDTO request) {
        return PageResponseDTO.of(mapper.selectList(request), request, mapper.count(request));
    }

    @Transactional
    public void register(CampaignTargetVO vo, String currentUserId) {
        vo.setRegId(currentUserId);
        vo.setStatus("WAIT");
        mapper.insertCampaign(vo);
    }

    @Transactional
    public void approve(Long campaignId, String aprvUser) {
        mapper.updateStatus(campaignId, "APPROVED", aprvUser);
    }

    @Transactional
    public void reject(Long campaignId, String aprvUser) {
        mapper.updateStatus(campaignId, "REJECTED", aprvUser);
    }

    @Transactional(readOnly = true)
    public List<CampaignTargetVO> getPending(String sendType) {
        return mapper.selectPending(sendType);
    }
}
