package com.example.sms.mapper.basic;

import com.example.sms.dto.basic.MessageSearchRequestDTO;
import com.example.sms.vo.basic.MessageVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MessageMapper {
    int count(MessageSearchRequestDTO request);
    List<MessageVO> selectList(MessageSearchRequestDTO request);
}
