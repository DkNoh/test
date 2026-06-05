package com.example.sms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import com.example.sms.mapper.EmployeeMapper;
import com.example.sms.vo.system.EmployeeVO;

/**
 * [Spring] @Configuration: 이 클래스가 스프링 컨테이너의 설정(Config) 파일임을 나타냅니다. 클래스 내부에 @Bean이 붙은 메서드들이 반환하는 객체들을 스프링이 관리하는 '빈(Bean)'으로 등록합니다.
 * [Spring] @EnableWebSecurity: 스프링 시큐리티 필터 체인을 활성화합니다. 웹 보안(인증, 인가, 로그인 등)을 설정할 수 있게 해줍니다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * [Spring] @Bean: 메서드가 반환하는 객체(SecurityFilterChain)를 스프링 컨테이너가 관리하는 전역 객체(빈)로 등록합니다.
     * 이 빈이 스프링 시큐리티의 모든 보안 규칙(어떤 URL은 막고, 어떤 URL은 열어둘지)을 관장합니다.
     *
     * @param http HttpSecurity 객체를 통해 각종 웹 보안 체계(CSRF, 로그인, 권한 등)를 커스터마이징합니다.
     * @param employeeMapper 로그인 성공 시 사용자 DB에서 접속 기록(IP, 시간)을 업데이트하기 위해 주입받습니다.
     * @return 조립이 완료된 SecurityFilterChain 객체
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, EmployeeMapper employeeMapper, com.example.sms.service.system.MenuService menuService) throws Exception {
        http
            /* 
             * [CSRF (Cross-Site Request Forgery) 방어 설정]
             * 현재 상태: 비활성화 (폐쇄망 환경 특성을 고려하여 개발 생산성 향상을 위해 OFF)
             * 
             * 향후 보안 심사 등으로 인해 활성화(ON)가 필요할 경우의 작업 가이드:
             * 1. 백엔드: 아래의 .csrf(AbstractHttpConfigurer::disable) 라인을 주석 처리 또는 삭제합니다. (Spring이 기본으로 켭니다)
             * 2. 프론트엔드 HTML (fragments/header.html 등):
             *    <head> 태그 안에 아래 메타 태그를 삽입합니다.
             *    <meta name="_csrf" th:content="${_csrf.token}"/>
             *    <meta name="_csrf_header" th:content="${_csrf.headerName}"/>
             * 3. 프론트엔드 JS (tui-common.js 등 공통 fetch 모듈):
             *    AJAX POST/PUT/DELETE 요청 시 헤더에 토큰을 주입합니다.
             *    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
             *    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
             *    headers[csrfHeader] = csrfToken;
             */
            .csrf(AbstractHttpConfigurer::disable)

            /*
             * [HTTP 요청 권한 인가 설정]
             * auth.requestMatchers(...).permitAll() : 해당 URL 패턴들은 인증 없이(로그인 안해도) 누구나 접근 가능합니다.
             * auth.anyRequest().authenticated() : 그 외의 모든 URL은 반드시 로그인된 사용자만 접근할 수 있습니다.
             */
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**", "/lib/**", "/img/**", "/vendor/**", "/error").permitAll()
                .anyRequest().authenticated()
            )

            /*
             * [폼 기반 로그인 설정]
             * form.loginPage("/login") : 인증이 필요한 페이지 접근 시 자동으로 튕겨갈 커스텀 로그인 폼 URL입니다.
             * form.successHandler(...) : 로그인이 성공했을 때 후처리 로직(IP 추적, 최근 접속일시 갱신, 세션값 세팅)을 수행합니다.
             */
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    String username = authentication.getName();
                    String ip = request.getHeader("X-Forwarded-For");
                    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                        ip = request.getRemoteAddr();
                    }
                    
                    java.time.LocalDateTime now = java.time.LocalDateTime.now();
                    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    
                    EmployeeVO emp = employeeMapper.findById(username);
                    if (emp != null) {
                        request.getSession().setAttribute("loginIp", ip);
                        request.getSession().setAttribute("loginTime", emp.getLastLoginTime() != null ? emp.getLastLoginTime().toString() : "첫 접속입니다");
                        request.getSession().setAttribute("userName", emp.getEmpName());

                        String role = emp.getUserRole() != null ? emp.getUserRole() : "ROLE_USER";
                        request.getSession().setAttribute("userRole", role);

                        java.util.List<com.example.sms.dto.MenuDTO> userMenus = menuService.getHierarchicalMenus(role);
                        request.getSession().setAttribute("userMenus", userMenus);

                        emp.setLastLoginIp(ip);
                        employeeMapper.updateLastLogin(emp);
                    } else {
                        // NOTE: 개발 환경의 인메모리(In-Memory) 임시 계정을 위한 분기처리
                        request.getSession().setAttribute("loginIp", ip);
                        request.getSession().setAttribute("loginTime", "첫 접속입니다");
                        request.getSession().setAttribute("userName", username);
                        
                        // 임시 계정(admin)은 ROLE_ADMIN 권한을 강제 부여하여 DB 메뉴를 불러옵니다.
                        request.getSession().setAttribute("userRole", "ROLE_ADMIN");
                        java.util.List<com.example.sms.dto.MenuDTO> userMenus = menuService.getHierarchicalMenus("ROLE_ADMIN");
                        request.getSession().setAttribute("userMenus", userMenus);
                    }
                    
                    response.sendRedirect("/");
                })
                .permitAll()
            )

            /*
             * [로그아웃 설정]
             * logout.logoutUrl("/logout") : 해당 URL 호출 시 스프링 시큐리티가 자동으로 로그아웃 처리를 가로챕니다.
             * invalidateHttpSession, deleteCookies : 세션을 무효화하고 JSESSIONID 쿠키를 삭제하여 세션 탈취를 방지합니다.
             */
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );
        return http.build();
    }

    /**
     * [비밀번호 암호화 빈]
     * 스프링 시큐리티는 인증을 수행할 때 반드시 PasswordEncoder가 필요합니다.
     * BCrypt 알고리즘을 사용해 단방향 해시 암호화를 수행합니다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 개발(dev) 환경: In-Memory 사용자 인증
    /**
     * [Spring] @Profile("dev"): application.yml에 spring.profiles.active=dev 로 설정되었을 때만 이 빈을 생성합니다.
     * 로컬 개발 시 복잡한 DB 설정 없이 간단하게 테스트하기 위해 메모리상에 admin 유저를 띄웁니다.
     */
    @Bean
    @Profile("dev")
    public UserDetailsService devUserDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin1234"))
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(admin);
    }
    
    /**
     * [운영(prod) 환경 가상 인증]
     * 향후 사내 시스템과 연동하기 위해 만들어둔 뼈대 코드입니다.
     * 실제 운영 반영 시 이 부분을 LdapBindAuthenticationManagerFactory 등으로 교체하여 사내 LDAP(AD) 인증을 연결합니다.
     */
    @Bean
    @Profile("prod")
    public UserDetailsService prodUserDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin1234")) // 임시
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(admin);
    }
}
