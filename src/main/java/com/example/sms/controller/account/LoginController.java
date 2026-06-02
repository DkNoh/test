package com.example.sms.controller.account;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * [LoginController]
 * 사용자 로그인 화면을 띄워주는 View 컨트롤러입니다.
 */
@Controller // 화면(HTML)을 반환하는 컨트롤러
public class LoginController {

    /**
     * 로그인 페이지 요청 처리
     * @return "login" (src/main/resources/templates/login.html 렌더링)
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
