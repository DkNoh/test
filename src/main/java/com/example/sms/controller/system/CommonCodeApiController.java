package com.example.sms.controller.system;

import com.example.sms.service.system.CommonCodeService;
import com.example.sms.vo.common.CommonCodeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import com.example.sms.dto.common.ApiResponse;

import java.util.List;

/**
 * [CommonCodeApiController]
 * 애플리케이션 전역에서 사용되는 '공통 코드(예: 부서 목록, 상태 코드 등)' 데이터를
 * JSON 형식으로 반환하는 역할을 전담하는 API 컨트롤러입니다.
 *
 * Q. API 컨트롤러로 분리한 이유는 무엇인가요?
 * A. 부서 콤보박스나 공통 분류 코드는 여러 화면(사용자 관리, 부서 관리 등)에서 중복으로 쓰입니다.
 *    만약 각 화면을 반환하는 일반 Controller마다 부서 목록을 조회해서 Model에 담아 내려보낸다면
 *    백엔드 코드가 심각하게 중복되고 무거워집니다.
 *    이것을 API로 분리해두면, 프론트엔드 자바스크립트가 화면을 먼저 가볍게 띄운 뒤(비동기 방식),
 *    필요할 때 공통 코드 API(`/api/common-code/...`)만 호출해서 콤보박스 선택지를 채워 넣을 수 있어 효율적입니다.
 */
@RestController // 이 클래스의 응답은 HTML 화면이 아닌, 순수 JSON 데이터임을 명시합니다.
@RequestMapping("/api/common-code") // '/api/common-code'로 시작하는 요청들을 이 컨트롤러가 전담합니다.
@RequiredArgsConstructor
public class CommonCodeApiController {

    private final CommonCodeService commonCodeService;

    /**
     * [공통 코드 목록 단건(Type별) 조회 API]
     * 
     * @param codeType 경로 변수(PathVariable)로 넘어온 코드의 종류 (예: 'dept' = 부서)
     * @return 해당 코드 종류에 속하는 상세 항목(예: 경영지원, 개발 등)들의 JSON 리스트
     */
    @GetMapping("/{codeType}")
    public ResponseEntity<ApiResponse<List<CommonCodeVO>>> getCodes(@PathVariable String codeType) {
        return ResponseEntity.ok(ApiResponse.success(commonCodeService.getCommonCodes(codeType)));
    }
}
