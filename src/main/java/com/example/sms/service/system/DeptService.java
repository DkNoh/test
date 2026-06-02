package com.example.sms.service.system;

import com.example.sms.dto.system.DeptSearchRequestDTO;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.mapper.system.DeptMapper;
import com.example.sms.vo.DeptVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeptService {

    private final DeptMapper deptMapper;

    @Transactional(readOnly = true)
    public PageResponseDTO<DeptVO> searchDepts(DeptSearchRequestDTO requestDTO) {
        int totalCount = deptMapper.countDepts(requestDTO);
        List<DeptVO> depts = deptMapper.selectDepts(requestDTO);
        return PageResponseDTO.of(depts, requestDTO, totalCount);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createDept(DeptVO deptVO) {
        if (deptVO.getActYn() == null) deptVO.setActYn("Y");
        deptMapper.insertDept(deptVO);
    }
}
