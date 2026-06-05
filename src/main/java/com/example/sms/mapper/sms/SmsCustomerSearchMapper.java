package com.example.sms.mapper.sms;

import com.example.sms.dto.sms.SmsCustomerSearchRequestDTO;
import com.example.sms.vo.sms.SmsCustomerSearchVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface SmsCustomerSearchMapper {
    int count(SmsCustomerSearchRequestDTO request);
    List<SmsCustomerSearchVO> selectList(SmsCustomerSearchRequestDTO request);
}
