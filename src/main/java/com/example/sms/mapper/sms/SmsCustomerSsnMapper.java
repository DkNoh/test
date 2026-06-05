package com.example.sms.mapper.sms;

import com.example.sms.vo.sms.SmsCustomerSsnVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface SmsCustomerSsnMapper {
    int count(String receiverNo);
    List<SmsCustomerSsnVO> selectList(String receiverNo);
}
