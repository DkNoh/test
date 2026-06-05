package com.example.sms.mapper.sms;

import com.example.sms.dto.common.PageRequestDTO;
import com.example.sms.vo.sms.CampaignTargetVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CampaignTargetMapper {
    int count(PageRequestDTO request);
    List<CampaignTargetVO> selectList(PageRequestDTO request);
    void insertCampaign(CampaignTargetVO vo);
    void updateStatus(@Param("campaignId") Long campaignId,
                      @Param("status")     String status,
                      @Param("aprvUser")   String aprvUser);
    List<CampaignTargetVO> selectPending(@Param("sendType") String sendType);
}
