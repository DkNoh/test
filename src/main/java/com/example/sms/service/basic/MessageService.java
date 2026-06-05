package com.example.sms.service.basic;

import com.example.sms.dto.basic.MessageSearchRequestDTO;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.mapper.basic.MessageMapper;
import com.example.sms.vo.basic.MessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper mapper;

    @Transactional(readOnly = true)
    public PageResponseDTO<MessageVO> search(MessageSearchRequestDTO request) {
        int totalCount = mapper.count(request);
        List<MessageVO> list = mapper.selectList(request);
        return PageResponseDTO.of(list, request, totalCount);
    }
}
