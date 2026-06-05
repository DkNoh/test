package com.example.sms.service.system;

import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.system.SystemMessageSearchRequestDTO;
import com.example.sms.mapper.system.SystemMessageMapper;
import com.example.sms.vo.system.SystemMessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemMessageService {

    private final SystemMessageMapper mapper;

    @Transactional(readOnly = true)
    public PageResponseDTO<SystemMessageVO> search(SystemMessageSearchRequestDTO request) {
        int totalCount = mapper.count(request);
        List<SystemMessageVO> list = mapper.selectList(request);
        return PageResponseDTO.of(list, request, totalCount);
    }
}
