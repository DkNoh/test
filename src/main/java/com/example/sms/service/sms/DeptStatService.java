package com.example.sms.service.sms;

import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.sms.DeptStatSearchRequestDTO;
import com.example.sms.mapper.sms.DeptStatMapper;
import com.example.sms.vo.sms.DeptStatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeptStatService {

    private final DeptStatMapper mapper;

    @Transactional(readOnly = true)
    public PageResponseDTO<DeptStatVO> search(DeptStatSearchRequestDTO request) {
        return PageResponseDTO.of(mapper.selectList(request), request, mapper.count(request));
    }

    @Transactional(readOnly = true)
    public List<DeptStatVO> getAll(String searchDate) {
        DeptStatSearchRequestDTO req = new DeptStatSearchRequestDTO();
        req.setSearchDate(searchDate);
        return mapper.selectList(req);
    }
}
