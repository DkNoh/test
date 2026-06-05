package com.example.sms.controller.account;

import com.example.sms.dto.account.EmployeeSearchRequestDTO;
import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.service.system.EmployeeService;
import com.example.sms.vo.system.EmployeeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/account/user-manage")
@RequiredArgsConstructor
public class UserManageController {

    private final EmployeeService employeeService;

    @GetMapping
    public String page() {
        return "account/user-manage";
    }

    @ResponseBody
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponseDTO<EmployeeVO>>> searchUsers(
            @RequestBody EmployeeSearchRequestDTO requestDTO) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.searchEmployees(requestDTO)));
    }

    @ResponseBody
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createUser(@RequestBody EmployeeVO employeeVO) {
        employeeService.createEmployee(employeeVO);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
