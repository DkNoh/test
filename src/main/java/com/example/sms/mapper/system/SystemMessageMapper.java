package com.example.sms.mapper.system;

import com.example.sms.dto.system.SystemMessageSearchRequestDTO;
import com.example.sms.vo.system.SystemMessageVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface SystemMessageMapper {
    int count(SystemMessageSearchRequestDTO request);
    List<SystemMessageVO> selectList(SystemMessageSearchRequestDTO request);
}
