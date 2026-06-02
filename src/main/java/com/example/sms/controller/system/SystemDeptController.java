package com.example.sms.controller.system;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/system")
public class SystemDeptController {

    /**
     * 부서 관리 페이지 반환
     */
    @GetMapping("/dept-manage")
    public String deptManagePage() {
        return "system/dept-manage";
    }
}
