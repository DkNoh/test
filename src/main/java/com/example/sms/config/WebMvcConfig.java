package com.example.sms.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * [스프링 Web MVC 설정]
 * 커스텀 인터셉터를 등록하고 동작 경로를 제어합니다.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final MenuAuthInterceptor menuAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // [동적 화면 접근 방어 인터셉터 등록]
        registry.addInterceptor(menuAuthInterceptor)
                .addPathPatterns("/**") // 기본적으로 모든 URL 찌르기에 대해 방어망 작동
                .excludePathPatterns(   // 검사가 불필요하거나 별도로 인증망을 거치는 예외 URL
                        "/", 
                        "/login", 
                        "/logout", 
                        "/css/**", 
                        "/js/**", 
                        "/lib/**",
                        "/vendor/**",
                        "/img/**", 
                        "/error",
                        "/api/**",      // 비동기 API 통신은 제외 (필요 시 별도 토큰/권한 필터 적용)
                        "/favicon.ico"
                );
    }
}
