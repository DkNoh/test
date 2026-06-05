---
paths:
  - "**/*Contact*.{java,xml}"
  - "**/*Group*.{java,xml}"
  - "**/templates/contact/**"
  - "**/static/js/contact/**"
---
# 주소록 (/contact)

- 연락처 CRUD(`TB_CONTACT`), 그룹(`TB_CONTACT_GROUP`), 매핑(`TB_GROUP_CONTACT_MAP`).
- 엑셀 다운로드 지원(`ExcelUtil`).
- 연락처(전화·이름)는 개인정보 → `@PrivacyLog` + `MaskingUtil` 필수.
