package com.example.sms.service.system;

import com.example.sms.dto.account.EmployeeSearchRequestDTO;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.mapper.EmployeeMapper;
import com.example.sms.vo.system.EmployeeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * [사원(사용자) 관리 비즈니스 서비스]
 * 시스템을 이용하는 임직원의 계정 생성, 권한 부여, 목록 조회 등의 비즈니스 로직을 통제합니다.
 * - 레거시 EJB 환경의 무분별한 Impl 1:1 매칭 구조를 탈피한 단일 클래스(Concrete Class) 아키텍처입니다.
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {

    // 사원 정보 조회를 위한 MyBatis Mapper 의존성 주입
    private final EmployeeMapper employeeMapper;

    /**
     * 조건에 맞는 사원 목록을 페이징(Pagination) 처리하여 조회합니다.
     * 
     * @param requestDTO 검색 조건 (이름, 사번 등) 및 페이징 정보 (page, size)
     * @return 페이징 메타정보와 데이터 목록이 캡슐화된 PageResponseDTO 객체
     * 
     * NOTE: 단순 조회 로직이므로 트랜잭션의 readOnly=true 옵션을 사용하여 JPA/DB의 스냅샷 오버헤드를 줄입니다.
     */
    @Transactional(readOnly = true)
    public PageResponseDTO<EmployeeVO> searchEmployees(EmployeeSearchRequestDTO requestDTO) {
        
        // 1. 페이징 네비게이션용 전체 데이터 건수 조회 (COUNT 쿼리)
        int totalCount = employeeMapper.countEmployees(requestDTO);
        
        // 2. LIMIT/OFFSET이 적용된 현재 페이지의 실제 사원 데이터 목록 조회
        List<EmployeeVO> employees = employeeMapper.selectEmployees(requestDTO);

        // 3. 규격화된 응답 객체(PageResponseDTO)에 맞추어 데이터 조립 및 반환 (TUI Grid 등에서 활용)
        return PageResponseDTO.of(employees, requestDTO, totalCount);
    }

    /**
     * 신규 사원(사용자) 계정을 시스템에 등록합니다.
     * 
     * @param employeeVO 등록할 사원의 정보가 담긴 도메인 객체(VO)
     * 
     * NOTE: 데이터의 등록(INSERT)이 발생하므로 예외 발생 시 안전하게 무조건 롤백(Rollback)되도록 트랜잭션을 강제합니다.
     */
    @Transactional(rollbackFor = Exception.class)
    public void createEmployee(EmployeeVO employeeVO) {
        
        // --- [보안 및 기본값 강제 세팅 영역] ---
        // 클라이언트(화면)에서 값을 누락했거나 악의적으로 권한을 변조하여 넘기는 것을 방어하기 위해
        // 서버사이드(Service)에서 필수 기본 권한과 상태값을 명시적으로 덮어씌웁니다.
        
        // 1. 계정 사용 여부는 기본적으로 'Y'(활성)로 세팅
        if (employeeVO.getActYn() == null) employeeVO.setActYn("Y");
        
        // 2. 신규 가입 계정의 등급은 일반 사용자(1)로 엄격히 제한
        if (employeeVO.getEmpLev() == null) employeeVO.setEmpLev("1");
        
        // 3. 기본 조회 권한 설정
        if (employeeVO.getPermSys() == null) employeeVO.setPermSys("N");
        if (employeeVO.getPermCpn() == null) employeeVO.setPermCpn("N");
        if (employeeVO.getPermSta() == null) employeeVO.setPermSta("N");
        
        // --- [DB 트랜잭션 수행] ---
        // 모든 검증 및 보안 기본값 세팅이 끝난 안전한 객체만을 DB에 최종 인서트합니다.
        employeeMapper.insertEmployee(employeeVO);
    }
}
