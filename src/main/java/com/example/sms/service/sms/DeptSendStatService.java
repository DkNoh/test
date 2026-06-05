package com.example.sms.service.sms;

import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.sms.DeptSendStatSearchRequestDTO;
import com.example.sms.mapper.sms.DeptSendStatMapper;
import com.example.sms.vo.sms.DeptSendStatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeptSendStatService {

    private final DeptSendStatMapper mapper;

    @Transactional(readOnly = true)
    public PageResponseDTO<DeptSendStatVO> search(DeptSendStatSearchRequestDTO request) {
        return PageResponseDTO.of(mapper.selectList(request), request, mapper.count(request));
    }
}
