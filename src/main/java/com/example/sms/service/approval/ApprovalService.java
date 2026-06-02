package com.example.sms.service.approval;

import com.example.sms.dto.ApprovalDTO;
import com.example.sms.dto.ApprovalSearchRequestDTO;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.mapper.ApprovalMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * [결재 관리 비즈니스 서비스]
 * 트랜잭션 경계(Transaction Boundary)를 설정하고 비즈니스 로직을 수행합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalMapper approvalMapper;

    /**
     * 결재 목록 페이징 조회
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public PageResponseDTO<ApprovalDTO> getApprovalList(ApprovalSearchRequestDTO request) {
        request.validate(); // page, size 기본값 세팅 및 검증
        long totalCount = approvalMapper.selectApprovalListCount(request);
        
        List<ApprovalDTO> list = java.util.Collections.emptyList();
        if (totalCount > 0) {
            list = approvalMapper.selectApprovalList(request);
        }
        return PageResponseDTO.of(list, request, totalCount);
    }

    /**
     * 결재 상세 조회
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public ApprovalDTO getApprovalDetail(String aprvId) {
        return approvalMapper.selectApprovalById(aprvId);
    }

    /**
     * 신규 결재 기안(상신)
     * NOTE: 상태 변경(DML)이 발생하므로 REQUIRED 선언 및 Exception 발생 시 무조건 Rollback 처리
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createApproval(ApprovalDTO dto) {
        return approvalMapper.insertApproval(dto);
    }

    /**
     * 결재 승인/반려 처리
     * NOTE: EJB CMT의 @TransactionAttribute(REQUIRED) 와 동일한 역할 수행
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int processApproval(ApprovalDTO dto) {
        // 실제 금융권 환경에서는 여기서 원본 테이블(예: 대상자 추출 테이블)의 상태도 함께 업데이트 합니다.
        // ex) if("APPROVED".equals(dto.getAprvStatus())) { targetMapper.updateTargetStatus("READY"); }
        
        return approvalMapper.updateApprovalStatus(dto);
    }
}
