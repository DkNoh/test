package com.example.sms.service.contact;

import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.contact.ContactSearchRequestDTO;
import com.example.sms.dto.contact.ContactVO;
import com.example.sms.mapper.contact.ContactMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactMapper mapper;

    @Transactional(readOnly = true)
    public PageResponseDTO<ContactVO> search(ContactSearchRequestDTO request) {
        int totalCount = mapper.count(request);
        List<ContactVO> list = mapper.selectList(request);
        return PageResponseDTO.of(list, request, totalCount);
    }

    @Transactional
    public void save(ContactVO vo) {
        mapper.insert(vo);
    }

    @Transactional
    public void delete(String id) {
        mapper.delete(id);
    }

    @Transactional(readOnly = true)
    public void downloadExcel(ContactSearchRequestDTO request, jakarta.servlet.http.HttpServletResponse response) {
        String[] headers = {"CONTACT_ID", "CONTACT_NM", "PHONE_NO", "COMPANY_NM", "GROUP_ID", "REG_DT"};
        String[] keys = {"CONTACT_ID", "CONTACT_NM", "PHONE_NO", "COMPANY_NM", "GROUP_ID", "REG_DT"};
        java.util.List<java.util.Map<String, Object>> list = mapper.selectListForExcel(request);
        com.example.sms.util.ExcelUtil.downloadExcel(response, "Contact_export", headers, list, keys);
    }
}
