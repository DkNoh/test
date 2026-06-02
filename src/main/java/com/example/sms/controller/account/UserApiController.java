package com.example.sms.controller.account;

import com.example.sms.dto.account.EmployeeSearchRequestDTO;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.service.system.EmployeeService;
import com.example.sms.vo.system.EmployeeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import com.example.sms.dto.common.ApiResponse;

/**
 * [UserApiController]
 * 사용자 관리 화면(계정 관리) 등에서 발생하는 데이터 처리를 전담하는 'API 전용 컨트롤러'입니다.
 *
 * Q. 왜 일반 @Controller가 아닌 @RestController(API)로 분리해서 생성하나요?
 * A. 일반 @Controller는 요청을 받으면 'HTML 화면(View)' 파일 자체를 반환합니다. 
 *    하지만 현대적인 웹 애플리케이션(SPA, AJAX 등)에서는 화면 이동(새로고침) 없이 화면 일부분(예: TUI Grid 등)만 
 *    업데이트하기 위해 데이터(JSON)만 서버와 주고받는 경우가 많습니다.
 *    이러한 '데이터 통신' 전용 통로를 일반 화면 반환 컨트롤러와 분리하여 @RestController로 만들면
 *    1) 프론트엔드와 백엔드의 역할이 명확해지고,
 *    2) 향후 모바일 앱이나 타 시스템에서 동일한 데이터(JSON)를 요구할 때 쉽게 API를 재사용할 수 있습니다.
 */
@RestController // 이 클래스의 모든 메서드 응답은 HTML 화면이 아닌, JSON 형태의 순수 데이터로 클라이언트(브라우저)에 반환됨을 선언합니다.
@RequestMapping("/api/user") // 브라우저가 '/api/user/...'로 시작하는 주소로 요청을 보내면 이 클래스가 처리하도록 매핑합니다.
@RequiredArgsConstructor // final로 선언된 필드들(employeeService)에 대해 자동으로 생성자를 만들어 의존성을 주입(DI)해 줍니다.
public class UserApiController {

    private final EmployeeService employeeService;

    /**
     * [사용자 목록 조회 API]
     * 프론트엔드(user-manage.js)에서 부서, 사용자명 등의 검색 조건을 JSON으로 보내면,
     * 이를 EmployeeSearchRequestDTO 객체로 받아 DB를 조회하고 페이징된 결과 데이터를 반환합니다.
     *
     * @param requestDTO 프론트엔드에서 보낸 검색 조건 및 페이징 정보 (JSON -> DTO 변환)
     * @return TUI Grid가 읽어들일 수 있도록 포맷팅된 사용자 데이터 세트 (JSON)
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponseDTO<EmployeeVO>>> searchUsers(@RequestBody EmployeeSearchRequestDTO requestDTO) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.searchEmployees(requestDTO)));
    }

    /**
     * [사용자 계정 생성 API]
     * 모달 팝업에서 입력한 신규 계정 정보(부서, 권한, 사용여부 등)를 JSON 형태로 받아와
     * employee 테이블에 Insert 하는 역할을 수행합니다.
     *
     * @param employeeVO 모달에서 입력된 사용자 데이터 (JSON -> VO 변환)
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createUser(@RequestBody EmployeeVO employeeVO) {
        employeeService.createEmployee(employeeVO);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
