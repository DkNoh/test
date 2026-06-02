package com.example.sms.config;

import com.example.sms.service.system.MenuService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * [동적 화면 접근 제어 인터셉터]
 * UI에서 버튼이 안 보여도, 악의적(혹은 실수)으로 주소창에 URL을 직접 타이핑하여 우회 접근하는 것을 완벽히 차단합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MenuAuthInterceptor implements HandlerInterceptor {

    private final MenuService menuService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        
        // 정적 리소스나 로그인 등 검증이 필요 없는 공용 URL은 통과 (WebMvcConfig 제외 패턴의 2차 방어)
        if (requestURI.startsWith("/css/") || requestURI.startsWith("/js/") || requestURI.startsWith("/lib/") ||
            requestURI.startsWith("/vendor/") || requestURI.startsWith("/img/") || requestURI.startsWith("/error") ||
            requestURI.startsWith("/api/") || requestURI.equals("/") || 
            requestURI.equals("/login") || requestURI.equals("/logout")) {
            return true;
        }

        // 세션에서 접속자의 권한(ROLE) 정보 추출
        String userRole = (String) request.getSession().getAttribute("userRole");
        if (userRole == null) {
            log.warn("[보안 경고] 세션 권한 누락. 미인가 접근 시도 URI: {}", requestURI);
            response.sendRedirect("/login");
            return false;
        }

        // [Two-Track 아키텍처 대응] 화면 URL 뒤에 붙는 데이터 통신용 API URI(/data, /auth-data, /tree-data 등)를 부모 화면 URL로 변환하여 검증
        String checkURI = requestURI;
        if (checkURI.matches(".*\\/(data|auth-data|tree-data|create|update|delete|process|search|excel|generate|save)$")) {
            checkURI = checkURI.substring(0, checkURI.lastIndexOf("/"));
        }

        // [동적 인가 검증] 현재 로그인한 사람의 권한이 이 URL에 매핑되어 있는지 실시간 DB 체크
        boolean hasAccess = menuService.hasAccess(checkURI, userRole);
        
        // [Master-Detail Fallback] 직접 권한이 없더라도, 현재 띄워진 부모 화면(Referer)에 권한이 있다면 하위 API 호출을 허용
        if (!hasAccess) {
            String referer = request.getHeader("Referer");
            if (referer != null) {
                try {
                    java.net.URL refererUrl = new java.net.URL(referer);
                    String refererPath = refererUrl.getPath();
                    if (menuService.hasAccess(refererPath, userRole)) {
                        hasAccess = true;
                        log.debug("[보안 허용] 인가된 부모 화면({})에서의 API 호출로 승인됨: {}", refererPath, requestURI);
                    }
                } catch (Exception e) {
                    log.warn("Referer 파싱 오류: {}", referer);
                }
            }
        }

        if (!hasAccess) {
            log.error("[보안 차단] 인가되지 않은 화면(URL) 무단 접근 시도. 대상URI: {}, 접속권한: {}", requestURI, userRole);
            
            // HTTP 403 Forbidden 에러 응답 -> Spring Error Page(/error/403 등)로 리다이렉션 됨
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: 해당 화면에 접근할 권한이 없습니다.");
            return false;
        }

        // 정상적으로 권한이 매핑되어 있으면 화면 렌더링 컨트롤러로 패스
        return true;
    }
}
