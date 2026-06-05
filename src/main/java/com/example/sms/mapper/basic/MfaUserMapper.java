package com.example.sms.mapper.basic;

import com.example.sms.dto.basic.MfaUserSearchRequestDTO;
import com.example.sms.vo.basic.MfaUserVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MfaUserMapper {
    int count(MfaUserSearchRequestDTO request);
    List<MfaUserVO> selectList(MfaUserSearchRequestDTO request);
}
