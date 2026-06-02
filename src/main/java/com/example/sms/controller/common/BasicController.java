package com.example.sms.controller.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/basic")
public class BasicController {
    
    @GetMapping("/notice")
    public String noticePage() {
        return "basic/notice";
    }
}
