---
paths:
  - "**/test/**/*.java"
---
# 테스트 규칙

- Service 단위 테스트는 Mockito로 Mapper를 목킹. given / when / then 구조.
- Mapper 테스트는 `@MybatisTest`(또는 `@SpringBootTest`) + dev/test 프로파일 DB에서.
- 실행: `mvn test`. 완료 보고는 테스트 PASS 로그가 있을 때만.
- 외부 연동(LDAP / JNDI)은 테스트에서 목킹하거나 프로파일로 분리.
