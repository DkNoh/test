package com.example.sms.mapper.sms;

import com.example.sms.dto.sms.CampaignHistorySearchRequestDTO;
import com.example.sms.vo.sms.CampaignHistoryVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CampaignHistoryMapper {
    int count(CampaignHistorySearchRequestDTO request);
    List<CampaignHistoryVO> selectList(CampaignHistorySearchRequestDTO request);
}
