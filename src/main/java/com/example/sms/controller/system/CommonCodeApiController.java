package com.example.sms.controller.system;

import com.example.sms.dto.common.ApiResponse;
import com.example.sms.service.system.CommonCodeService;
import com.example.sms.vo.common.CommonCodeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/common-code")
@RequiredArgsConstructor
public class CommonCodeApiController {

    private final CommonCodeService commonCodeService;

    @GetMapping("/{codeType}")
    public ResponseEntity<ApiResponse<List<CommonCodeVO>>> getCodes(@PathVariable String codeType) {
        return ResponseEntity.ok(ApiResponse.success(commonCodeService.getCommonCodes(codeType)));
    }

    /** 업무분류 캐스케이드: 부서 선택 → 해당 부서의 메세지 목록 */
    @GetMapping("/messages")
    public ResponseEntity<ApiResponse<List<CommonCodeVO>>> getMessagesByDept(
            @RequestParam(required = false) String deptId) {
        return ResponseEntity.ok(ApiResponse.success(commonCodeService.getMessagesByDept(deptId)));
    }

    /**
     * 은행코드 검색
     * keyword 없으면 전체 목록, 있으면 코드·명 부분 일치 검색
     * GET /api/common-code/bank          → 전체 (콤보용)
     * GET /api/common-code/bank?keyword=KE → 타이핑 자동완성용
     */
    @GetMapping("/bank")
    public ResponseEntity<ApiResponse<List<CommonCodeVO>>> getBanks(
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(ApiResponse.success(commonCodeService.getBanksByKeyword(keyword)));
    }
}
