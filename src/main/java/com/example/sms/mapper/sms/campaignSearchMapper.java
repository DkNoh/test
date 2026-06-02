package com.example.sms.mapper.sms;

import com.example.sms.dto.sms.campaignSearchSearchRequestDTO;
import com.example.sms.vo.sms.CampaignSearchVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface campaignSearchMapper {
    int count(campaignSearchSearchRequestDTO request);
    List<CampaignSearchVO> selectList(campaignSearchSearchRequestDTO request);
}
