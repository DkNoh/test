package com.example.sms.controller.system;

import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.system.DeptSearchRequestDTO;
import com.example.sms.service.system.DeptService;
import com.example.sms.vo.system.DeptVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/system/dept-manage")
@RequiredArgsConstructor
public class DeptManageController {

    private final DeptService deptService;

    @GetMapping
    public String page() {
        return "system/dept-manage";
    }

    @ResponseBody
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponseDTO<DeptVO>>> searchDepts(
            @RequestBody DeptSearchRequestDTO requestDTO) {
        return ResponseEntity.ok(ApiResponse.success(deptService.searchDepts(requestDTO)));
    }

    @ResponseBody
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createDept(@RequestBody DeptVO deptVO) {
        deptService.createDept(deptVO);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
