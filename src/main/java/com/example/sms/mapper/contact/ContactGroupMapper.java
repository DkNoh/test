package com.example.sms.mapper.contact;

import com.example.sms.dto.contact.ContactGroupSearchRequestDTO;
import com.example.sms.vo.contact.ContactGroupVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface ContactGroupMapper {
    /**
     * 조건에 맞는 전체 데이터 건수 조회
     */
    int count(ContactGroupSearchRequestDTO request);

    /**
     * 페이징 처리된 목록 데이터 조회
     */
    List<ContactGroupVO> selectList(ContactGroupSearchRequestDTO request);

    /**
     * 신규 데이터 등록
     */
    int insert(ContactGroupVO vo);

    /**
     * 기존 데이터 수정
     */
    int update(ContactGroupVO vo);

    /**
     * 데이터 삭제
     */
    int delete(String id);

    /**
     * 엑셀 다운로드용 전체 데이터 조회 (페이징 없음)
     */
    List<Map<String, Object>> selectListForExcel(ContactGroupSearchRequestDTO request);
}