package com.example.sms.mapper.sms;

import com.example.sms.dto.sms.DeptStatSearchRequestDTO;
import com.example.sms.vo.sms.DeptStatVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DeptStatMapper {
    int count(DeptStatSearchRequestDTO request);
    List<DeptStatVO> selectList(DeptStatSearchRequestDTO request);
}
