package com.example.sms.service.sms;

import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.sms.DeptStatSearchRequestDTO;
import com.example.sms.dto.sms.DeptStatVO;
import com.example.sms.mapper.sms.DeptStatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeptStatService {

    private final DeptStatMapper mapper;

    @Transactional(readOnly = true)
    public PageResponseDTO<DeptStatVO> search(DeptStatSearchRequestDTO request) {
        int totalCount = mapper.count(request);
        List<DeptStatVO> list = mapper.selectList(request);
        return PageResponseDTO.of(list, request, totalCount);
    }

    @Transactional
    public void save(DeptStatVO vo) {
        // TODO: 신규/수정 분기 로직 구현
        mapper.insert(vo);
    }

    @Transactional
    public void delete(String id) {
        mapper.delete(id);
    }

    @Transactional(readOnly = true)
    public void downloadExcel(DeptStatSearchRequestDTO request, jakarta.servlet.http.HttpServletResponse response) {
        String[] headers = {"DEPT_ID", "DEPT_NM", "TOTAL_SEND_CNT", "TOTAL_SUCCESS_CNT", "TOTAL_FAIL_CNT"};
        String[] keys = {"deptId", "deptNm", "totalSendCnt", "totalSuccessCnt", "totalFailCnt"};
        java.util.List<java.util.Map<String, Object>> list = mapper.selectListForExcel(request);
        com.example.sms.util.ExcelUtil.downloadExcel(response, "DeptStat_export", headers, list, keys);
    }
}
