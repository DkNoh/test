package com.example.sms.mapper.system;

import com.example.sms.dto.system.DeptSearchRequestDTO;
import com.example.sms.vo.system.DeptVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeptMapper {
    List<DeptVO> selectDepts(DeptSearchRequestDTO request);
    int countDepts(DeptSearchRequestDTO request);
    void insertDept(DeptVO dept);
    void updateDept(DeptVO dept);
    DeptVO findById(String depId);
}
