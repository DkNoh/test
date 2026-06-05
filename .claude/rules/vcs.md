# 버전관리 / 커밋

- 루트에 `.git/`이 있으면 Git, `.svn/`이 있으면 SVN. 둘 다 없으면 사용자에게 확인.
- 커밋 전: 변경 파일 목록 + 커밋 메시지 전문을 보여주고 **승인 후에만** 커밋한다.
- 메시지 형식: `feat|fix|refactor|docs|chore(scope): 한 줄 요약` + 변경 bullet. 이슈가 있으면 `KEY-123: 제목`.
- `main`/`master` 직접 커밋·force push 금지 (사용자가 명시 요청한 경우만 예외).
