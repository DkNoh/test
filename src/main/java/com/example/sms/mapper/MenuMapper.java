package com.example.sms.mapper;

import com.example.sms.dto.MenuDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * [동적 화면 접근 제어 Mapper]
 * 사용자 권한별로 노출할 메뉴 목록과 URL 접근 인가(Authorization) 여부를 DB에서 조회합니다.
 */
@Mapper
public interface MenuMapper {
    
    /**
     * 특정 권한(Role)이 접근할 수 있는 전체 메뉴 목록을 조회합니다.
     * UI의 좌측 사이드바(LNB)를 동적으로 그릴 때 사용됩니다.
     * 
     * @param authCd 접속자의 권한코드 (예: ROLE_ADMIN)
     * @return 권한에 매핑된 활성 메뉴 목록
     */
    List<MenuDTO> selectMenusByAuthCode(@Param("authCd") String authCd);
    
    /**
     * 특정 URL에 접근하려는 사용자의 권한이 유효한지 검증합니다.
     * Interceptor(메뉴 직타 방어망)에서 실시간으로 사용됩니다.
     * 
     * @param menuUrl 사용자가 주소창에 친 URL (예: /authority/manage)
     * @param authCd 사용자의 권한코드
     * @return 접근 가능한 매핑 데이터 건수 (0이면 접근 불가, 1이상이면 접근 허용)
     */
    int countAuthForUrl(@Param("menuUrl") String menuUrl, @Param("authCd") String authCd);
}
