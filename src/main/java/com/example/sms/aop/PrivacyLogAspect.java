package com.example.sms.aop;

import com.example.sms.annotation.PrivacyLog;
import com.example.sms.service.system.AuditLogService;
import com.example.sms.vo.system.PrivacyAuditLogVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PrivacyLogAspect {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper = new ObjectMapper(); // 파라미터를 JSON으로 직렬화

    @Around("@annotation(privacyLog)")
    public Object doAuditLog(ProceedingJoinPoint joinPoint, PrivacyLog privacyLog) throws Throwable {
        
        // 1. 요청 정보 가져오기
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String requestUrl = request.getRequestURI();
        
        // 2. 실행자(로그인된 사용자) 정보 가져오기 (SecurityContext 기반)
        String executorId = "SYSTEM";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            executorId = auth.getName(); // 유저 아이디
        }

        // 3. 실행자 IP 주소 가져오기
        String executorIp = request.getHeader("X-Forwarded-For");
        if (executorIp == null) {
            executorIp = request.getRemoteAddr();
        }

        // 4. 대상 데이터 (메서드에 넘겨진 파라미터들) JSON 직렬화
        String targetData = "";
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                // 파라미터가 길 수 있으므로 500자로 자르기
                targetData = objectMapper.writeValueAsString(args);
                if (targetData.length() > 490) {
                    targetData = targetData.substring(0, 490) + "...";
                }
            }
        } catch (Exception e) {
            targetData = "파라미터 직렬화 실패";
        }

        // 5. DB에 저장할 VO 객체 조립
        PrivacyAuditLogVO logVO = PrivacyAuditLogVO.builder()
                .executorId(executorId)
                .executorIp(executorIp)
                .requestUrl(requestUrl)
                .actionType(privacyLog.action())
                .targetData(targetData)
                .build();

        // 6. DB에 로그 저장 (비동기로 빼도 좋음)
        auditLogService.saveLog(logVO);

        // 7. 원래 호출하려던 핵심 비즈니스 로직(예: 조회) 계속 실행
        return joinPoint.proceed();
    }
}