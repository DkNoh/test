package com.example.sms.mapper.sms;

import com.example.sms.dto.sms.DeptStatSearchRequestDTO;
import com.example.sms.dto.sms.DeptStatVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface DeptStatMapper {
    int count(DeptStatSearchRequestDTO request);
    List<DeptStatVO> selectList(DeptStatSearchRequestDTO request);

    int insert(DeptStatVO vo);
    int update(DeptStatVO vo);
    int delete(String id);

    List<Map<String, Object>> selectListForExcel(DeptStatSearchRequestDTO request);
}
