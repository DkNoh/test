package com.example.sms.mapper.sms;

import com.example.sms.dto.sms.DeptSendStatSearchRequestDTO;
import com.example.sms.vo.sms.DeptSendStatVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeptSendStatMapper {
    int count(DeptSendStatSearchRequestDTO request);
    List<DeptSendStatVO> selectList(DeptSendStatSearchRequestDTO request);
}
