package com.example.sms.mapper;

import com.example.sms.dto.ApprovalDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

/**
 * [결재 관리 Mapper]
 * TB_APPROVAL 및 TB_APPROVAL_HIST 테이블에 대한 데이터 접근을 담당합니다.
 */
@Mapper
public interface ApprovalMapper {
    
    /**
     * 결재 목록 총 카운트 조회
     */
    long selectApprovalListCount(com.example.sms.dto.ApprovalSearchRequestDTO request);

    /**
     * 결재 목록 조회 (서버사이드 페이징 적용)
     */
    List<ApprovalDTO> selectApprovalList(com.example.sms.dto.ApprovalSearchRequestDTO request);
    
    /**
     * 결재 단건 상세 조회
     */
    ApprovalDTO selectApprovalById(String aprvId);
    
    /**
     * 신규 결재 상신 (기안)
     */
    int insertApproval(ApprovalDTO approvalDTO);
    
    /**
     * 결재 상태 변경 (승인/반려 처리)
     */
    int updateApprovalStatus(ApprovalDTO approvalDTO);
}
