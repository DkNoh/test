package com.example.sms.mapper.contact;

import com.example.sms.dto.contact.ContactSearchRequestDTO;
import com.example.sms.vo.contact.ContactVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface ContactMapper {
    int count(ContactSearchRequestDTO request);
    List<ContactVO> selectList(ContactSearchRequestDTO request);
    int insert(ContactVO vo);
    int update(ContactVO vo);
    int delete(String id);
    List<Map<String, Object>> selectListForExcel(ContactSearchRequestDTO request);
}
