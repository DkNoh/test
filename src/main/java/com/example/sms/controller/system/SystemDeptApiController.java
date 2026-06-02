package com.example.sms.controller.system;

import com.example.sms.dto.system.DeptSearchRequestDTO;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.service.system.DeptService;
import com.example.sms.vo.DeptVO;
import com.example.sms.dto.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dept")
@RequiredArgsConstructor
public class SystemDeptApiController {

    private final DeptService deptService;

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponseDTO<DeptVO>>> searchDepts(@RequestBody DeptSearchRequestDTO requestDTO) {
        return ResponseEntity.ok(ApiResponse.success(deptService.searchDepts(requestDTO)));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createDept(@RequestBody DeptVO deptVO) {
        deptService.createDept(deptVO);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
