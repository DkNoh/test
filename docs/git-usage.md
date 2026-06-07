# Git Usage Guide

이 문서는 `sms-project-v2`에서 git을 사용할 때의 기본 기준이다.

## 기본 원칙

- 기능 변경, 문서 변경, DB seed/export 변경은 서로 다른 커밋으로 분리한다.
- 빌드 산출물, 로그, IDE 설정, 개인 환경 파일은 커밋하지 않는다.
- 이미 git에 올라간 파일은 `.gitignore`만 추가해도 추적이 멈추지 않는다. 필요하면 `git rm --cached`로 index에서 제거한다.
- 운영 DB 접속 정보, 비밀번호, 개인 메모는 커밋하지 않는다.

## 커밋 대상

커밋한다.

- `src/**`
- `pom.xml`
- `src/main/resources/mapper/**/*.xml`
- `src/main/resources/templates/**/*.html`
- `src/main/resources/static/**`
- `src/main/resources/application.yml`의 공통/환경변수 기반 설정
- `docs/**`
- `HANDOFF.md`
- `tools/**`
- `db/export/**` 중 로컬 개발 재현용 더미 SQL

커밋하지 않는다.

- `target/**`
- `logs/**`
- `.settings/**`, `.idea/**`, `.vscode/**`
- `.classpath`, `.project`, `.factorypath`
- `.env`, `.env.*`
- `Codex.local.md`, `CLAUDE.local.md`
- `db/local/**`
- `*.dmp`, `*.dump`

## DB 파일 정책

`db/export/`

- 다른 로컬 PC에서도 같은 개발 상태를 재현하기 위한 공유용 SQL을 둔다.
- 실제 운영 데이터가 아니라 더미/개발 데이터일 때만 커밋한다.
- 예: `db/export/sms-project-v2-project-tables.sql`

`db/local/`

- 개인 PC에서 임시로 만든 export/import 파일을 둔다.
- git ignore 대상이다.
- 운영/민감 데이터, 임시 dump, 테스트 중간 산출물은 여기에 둔다.

## 로그 정리

로그는 `.gitignore` 대상이지만, 이미 추적 중이었다면 한 번만 index에서 제거한다.

```powershell
git rm --cached logs/sms-project*.log
```

파일은 로컬 디스크에 남고 git 추적만 해제된다.

## 추천 커밋 흐름

1. 변경 상태 확인

```powershell
git status --short
```

2. 관련 파일만 stage

```powershell
git add <file1> <file2>
```

3. stage 내용 확인

```powershell
git diff --cached --name-status
git diff --cached
```

4. 커밋

```powershell
git commit -m "type: summary"
```

## 커밋 메시지 예시

- `feat: add sms history resend API`
- `fix: correct sms history search condition`
- `docs: add local db sync guide`
- `chore: update gitignore for local artifacts`
- `chore: add local oracle sql export`

## 다른 PC에서 이어받기

1. 최신 커밋을 pull 한다.
2. 필요한 경우 `db/export/sms-project-v2-project-tables.sql`을 로컬 Oracle에 실행한다.
3. 로컬 설정은 환경변수 또는 개인 파일에 둔다.
4. 작업 시작 전 `git status --short`로 깨끗한 상태인지 확인한다.

## 주의

- `db/export/**`에 들어가는 SQL도 실제 고객/운영 데이터라면 커밋하지 않는다.
- `drop table ... cascade constraints purge;`가 포함된 SQL은 로컬 초기화용으로만 사용한다.
- unrelated 변경이 있을 때는 `git add .`를 사용하지 않는다.
