package com.example.sms.mapper.sms;

import com.example.sms.dto.sms.campaignSearchSearchRequestDTO;
import com.example.sms.dto.sms.campaignSearchVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface campaignSearchMapper {
    int count(campaignSearchSearchRequestDTO request);
    List<campaignSearchVO> selectList(campaignSearchSearchRequestDTO request);
}
