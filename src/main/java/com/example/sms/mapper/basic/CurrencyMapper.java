package com.example.sms.mapper.basic;

import com.example.sms.dto.basic.CurrencySearchRequestDTO;
import com.example.sms.vo.basic.CurrencyVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface CurrencyMapper {

    int count(CurrencySearchRequestDTO request);

    List<CurrencyVO> selectList(CurrencySearchRequestDTO request);

    int insert(CurrencyVO vo);

    int update(CurrencyVO vo);

    int delete(String id);

    List<Map<String, Object>> selectListForExcel(CurrencySearchRequestDTO request);
}    