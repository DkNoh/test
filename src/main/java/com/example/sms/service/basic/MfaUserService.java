package com.example.sms.service.basic;

import com.example.sms.dto.basic.MfaUserSearchRequestDTO;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.mapper.basic.MfaUserMapper;
import com.example.sms.vo.basic.MfaUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MfaUserService {

    private final MfaUserMapper mapper;

    @Transactional(readOnly = true)
    public PageResponseDTO<MfaUserVO> search(MfaUserSearchRequestDTO request) {
        int totalCount = mapper.count(request);
        List<MfaUserVO> list = mapper.selectList(request);
        return PageResponseDTO.of(list, request, totalCount);
    }
}
