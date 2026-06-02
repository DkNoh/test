package com.example.sms.service.system;

import com.example.sms.mapper.CommonCodeMapper;
import com.example.sms.vo.common.CommonCodeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * [공통 코드 관리 비즈니스 서비스]
 * 시스템 전반에서 사용되는 공통 코드(부서, 직급, 상태값 등)를 관리하고 제공하는 비즈니스 로직을 처리합니다.
 * - 향상된 성능을 위해 Spring Cache(@Cacheable)를 적극 활용하여 DB 부하를 경감합니다.
 * - 무의미한 Impl 분리 안티 패턴을 제거한 단일 구현체(Concrete Class) 구조입니다.
 */
@Service
@RequiredArgsConstructor
public class CommonCodeService {

    // 데이터베이스 접근을 위한 MyBatis Mapper 객체 주입
    private final CommonCodeMapper commonCodeMapper;

    /**
     * 특정 코드 유형(CodeType)에 해당하는 공통 코드 목록을 조회합니다.
     * 
     * @param codeType 조회할 공통 코드의 카테고리 (예: "dept" - 부서코드)
     * @return 해당 유형에 속하는 공통 코드 목록 (CommonCodeVO 리스트)
     * 
     * NOTE: 'commonCode'라는 캐시 영역에서 파라미터 'codeType'을 키(Key)로 사용합니다. 
     *       캐시 히트(Hit) 시 DB(MyBatis) 조회를 생략하고 즉시 반환하여 성능 최적화를 달성합니다.
     */
    @Cacheable(value = "commonCode", key = "#codeType")
    public List<CommonCodeVO> getCommonCodes(String codeType) {
        
        // 1. 요청된 코드 타입이 '부서(dept)'인 경우
        if ("dept".equalsIgnoreCase(codeType)) {
            // 현재 사용 중(활성 상태)인 부서 목록만 DB에서 퍼와서 반환합니다.
            return commonCodeMapper.selectActiveDepartments();
        }
        
        // 2. 정의되지 않은 코드 타입이 들어온 경우 (예외를 던지지 않고 빈 리스트 반환하여 장애 방지)
        // 향후 권한(auth), 직급(position) 등의 코드가 추가되면 분기를 안전하게 확장할 수 있습니다.
        return new ArrayList<>();
    }
}
