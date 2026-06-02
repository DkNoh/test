package com.example.sms.mapper.sms;

import com.example.sms.dto.sms.HistoryTestSearchRequestDTO;
import com.example.sms.vo.sms.HistoryTestVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface HistoryTestMapper {
    int count(HistoryTestSearchRequestDTO request);
    List<HistoryTestVO> selectList(HistoryTestSearchRequestDTO request);
}
