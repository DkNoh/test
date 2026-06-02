package com.example.sms.controller.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * [IndexController]
 * 시스템의 메인 화면(대시보드)으로 이동하는 역할을 담당하는 'View 전용 컨트롤러'입니다.
 *
 * Q. API 컨트롤러(@RestController)와 다른 점은 무엇인가요?
 * A. API 컨트롤러는 순수 데이터(JSON)만 반환하지만, 일반 @Controller는 클라이언트(브라우저)에게
 *    HTML 화면(Thymeleaf 템플릿)을 반환합니다. 
 *    즉, 브라우저가 처음에 '어떤 화면을 그릴지'를 서버에게 요청할 때 이 컨트롤러가 응답합니다.
 */
@Controller
public class IndexController {

    /**
     * 기본 루트 URL("/") 접속 시 메인 화면(home.html)을 반환합니다.
     */
    @GetMapping("/")
    public String index() {
        return "index"; // src/main/resources/templates/index.html 파일을 렌더링하여 브라우저에 전송
    }
}
