---
paths:
  - "**/*SystemMessage*.{java,xml}"
  - "**/*KakaoTemplate*.{java,xml}"
  - "**/*AdMessage*.{java,xml}"
  - "**/templates/system/message-manage*.html"
  - "**/templates/system/kakao-template*.html"
  - "**/templates/system/ad-message*.html"
---
# 시스템관리 - 메세지 관련 (/system/message-manage, /system/kakao-template, /system/ad-message)

세 컨트롤러 모두 `SystemMessageService` + `SystemMessageVO`를 공유한다.
`MSG_TYPE` 값으로 데이터를 구분하며, 컨트롤러에서 `request.setMsgType(...)` 후 서비스 호출.

| URL | 컨트롤러 | MSG_TYPE |
|-----|----------|----------|
| `/system/message-manage` | `SystemMessageController` | (전체) |
| `/system/kakao-template` | `SystemKakaoTemplateController` | `KAKAO` |
| `/system/ad-message` | `SystemAdMessageController` | `AD` |

- 테이블: `TB_MESSAGE`.
- 카카오템플릿 전환 / NON 카카오 전환 — 타입 변경은 `MSG_TYPE` 업데이트로 처리.
- 광고성 메세지: 등록/해제 담당자 행번, 일시 기록 필요.
