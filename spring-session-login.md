# Spring Security Session Login Overview

## 1. 개요
Spring Security는 기본적으로 세션 기반 인증(Session-Based Authentication)을 제공합니다. 사용자가 자격 증명(ID/Password)을 제공하여 인증에 성공하면, 서버는 해당 사용자를 위한 세션을 생성하고 `JSESSIONID` 쿠키를 클라이언트(웹 브라우저)에 발급합니다. 이후의 요청은 이 쿠키를 통해 인증된 사용자로 인식됩니다.

## 2. 인증 과정 (Authentication Flow)
1. **사용자 요청**: 사용자가 인증이 필요한 페이지(예: `/`, `/history`)에 접근합니다.
2. **필터 개입 (`ExceptionTranslationFilter`)**: 인증되지 않은 사용자이므로 접근이 거부되고, 커스텀 로그인 페이지(`/login`)로 리다이렉트 됩니다.
3. **자격 증명 제출**: 사용자가 로그인 폼에서 아이디와 비밀번호를 입력 후 제출합니다. (`POST /login`)
4. **`UsernamePasswordAuthenticationFilter` 동작**: 
   - 요청에서 아이디와 비밀번호를 추출하여 `UsernamePasswordAuthenticationToken` 객체를 생성합니다.
   - 이를 `AuthenticationManager`에게 전달하여 인증을 위임합니다.
5. **`AuthenticationProvider` 검증**:
   - `dev` 프로필: `InMemoryUserDetailsManager`에서 사용자를 찾아 `PasswordEncoder`로 암호화된 비밀번호와 입력된 비밀번호를 비교합니다.
   - `prod` 프로필: `LdapAuthenticationProvider`가 설정된 가상/실제 LDAP 서버를 통해 자격 증명을 검증합니다.
6. **인증 성공 및 세션 저장 (`SecurityContextHolder`)**:
   - 인증이 성공하면 `Authentication` 객체가 반환되고, 애플리케이션 전반에서 사용할 수 있도록 `SecurityContext`에 저장됩니다.
   - 이 정보는 서블릿 세션에 저장되며(기본적으로 `HttpSessionSecurityContextRepository` 활용), 클라이언트는 `JSESSIONID` 쿠키를 받습니다.
7. **성공 리다이렉트**: 인증 성공 후 `SecurityConfig`에 설정된 기본 성공 URL(`/`)로 자동 이동합니다.

## 3. 주요 설정 내용 (SecurityConfig)
- **`SecurityFilterChain`**: HTTP 요청에 대한 보안 필터 체인을 정의합니다. `permitAll()`을 통해 로그인 페이지(`/login`), 정적 자원(`/css/**`, `/js/**` 등)의 인증을 면제합니다.
- **`UserDetailsService` (개발용)**: 애플리케이션 시작 시 메모리 내에 테스트 사용자(`admin`) 정보를 정의하여 빠른 개발을 지원합니다.
- **`formLogin()`**: 폼 기반 로그인을 활성화합니다. 커스텀 로그인 페이지 위치와 로그인 성공 후 이동할 경로(`defaultSuccessUrl("/", true)`)를 지정합니다.
- **`logout()`**: `/logout` 경로로의 요청 시 로그아웃을 처리하며, 세션 무효화(`invalidateHttpSession(true)`) 및 `JSESSIONID` 쿠키 삭제(`deleteCookies`)를 수행합니다.
