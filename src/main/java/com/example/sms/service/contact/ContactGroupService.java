package com.example.sms.service.contact;

import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.contact.ContactGroupSearchRequestDTO;
import com.example.sms.vo.contact.ContactGroupVO;
import com.example.sms.mapper.contact.ContactGroupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactGroupService {

    private final ContactGroupMapper mapper;

    /**
     * 목록 조회 및 페이징 처리
     */
    @Transactional(readOnly = true)
    public PageResponseDTO<ContactGroupVO> search(ContactGroupSearchRequestDTO request) {
        int totalCount = mapper.count(request);
        List<ContactGroupVO> list = mapper.selectList(request);
        return PageResponseDTO.of(list, request, totalCount);
    }

    /**
     * 데이터 저장 (신규 등록 및 수정)
     */
    @Transactional
    public void save(ContactGroupVO vo) {
        // TODO: 신규/수정 분기 로직 구현
        mapper.insert(vo);
    }

    /**
     * 데이터 삭제
     */
    @Transactional
    public void delete(String id) {
        mapper.delete(id);
    }

    /**
     * 대용량 엑셀 다운로드 처리
     */
    @Transactional(readOnly = true)
    public void downloadExcel(ContactGroupSearchRequestDTO request, jakarta.servlet.http.HttpServletResponse response) {
        String[] headers = {"GROUP_ID", "GROUP_NM", "GROUP_DESC", "USE_YN", "REG_ID", "REG_DT"};
        String[] keys = {"GROUP_ID", "GROUP_NM", "GROUP_DESC", "USE_YN", "REG_ID", "REG_DT"};
        java.util.List<java.util.Map<String, Object>> list = mapper.selectListForExcel(request);
        com.example.sms.util.ExcelUtil.downloadExcel(response, "ContactGroup_export", headers, list, keys);
    }
}