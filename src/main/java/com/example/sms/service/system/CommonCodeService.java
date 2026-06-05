package com.example.sms.service.system;

import com.example.sms.mapper.CommonCodeMapper;
import com.example.sms.vo.common.CommonCodeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonCodeService {

    private final CommonCodeMapper commonCodeMapper;

    public List<CommonCodeVO> getCommonCodes(String codeType) {
        if ("dept".equalsIgnoreCase(codeType)) {
            return commonCodeMapper.selectActiveDepartments();
        }
        return new ArrayList<>();
    }

    public List<CommonCodeVO> getMessagesByDept(String deptId) {
        return commonCodeMapper.selectMessagesByDept(deptId);
    }

    /** 은행코드 검색 — keyword 없으면 전체 목록 */
    public List<CommonCodeVO> getBanksByKeyword(String keyword) {
        return commonCodeMapper.selectBanksByKeyword(keyword);
    }
}
