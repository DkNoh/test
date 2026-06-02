package com.example.sms.controller.account;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * [AccountController]
 * 계정 관리(사용자 관리 등)와 관련된 화면들을 브라우저에 렌더링하는 View 컨트롤러입니다.
 * 실제 데이터 조작(조회, 생성)은 UserApiController 등 별도의 API에서 JSON 통신으로 수행합니다.
 */
@Controller // 화면(HTML) 반환 전용 컨트롤러
@RequestMapping("/account")
public class AccountController {

    @GetMapping("/user-manage")
    public String userManage() {
        return "account/user-manage";
    }
}
