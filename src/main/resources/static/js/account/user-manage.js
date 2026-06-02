/**
 * user-manage.js
 * TuiPageBuilder를 사용하여 모던하게 재작성된 계정 관리 화면 스크립트입니다.
 */
document.addEventListener('DOMContentLoaded', function () {

    // 1. TuiPageBuilder를 이용한 메인 그리드 렌더링
    const pageBuilder = new TuiPageBuilder({
        el: 'grid',
        apiUrl: '/api/user/search', // UserApiController의 /api/user/search 엔드포인트
        searchInputs: ['empId', 'empNm', 'depId'],
        rowHeaders: ['rowNum'],
        columns: [
            { header: '사번(ID)', name: 'empId', width: 120, align: 'center' },
            { header: '사원명', name: 'empNm', minWidth: 120, align: 'center' },
            { header: '부서코드', name: 'depId', width: 100, align: 'center' },
            { header: '사원등급', name: 'empLev', width: 100, align: 'center',
              formatter: ({value}) => value === '9' ? '시스템관리자' : (value === '2' ? '부서관리자' : '일반사용자') },
            { header: '시스템권한', name: 'permSys', width: 90, align: 'center' },
            { header: '캠페인권한', name: 'permCpn', width: 90, align: 'center' },
            { header: '통계권한', name: 'permSta', width: 90, align: 'center' },
            { header: '사용여부', name: 'actYn', width: 90, align: 'center',
              formatter: ({value}) => value === 'Y' ? '<span class="badge badge-success">사용</span>' : '<span class="badge badge-fail">미사용</span>' },
            { header: '최근접속', name: 'lastLoginDttm', width: 160, align: 'center' }
        ]
    });

    // 2. 모달 제어 로직 (계정 생성)
    document.querySelector('#btn-create').addEventListener('click', () => {
        // 필드 초기화
        document.querySelector('#c-empId').value = '';
        document.querySelector('#c-empNm').value = '';
        document.querySelector('#c-depId').value = '';
        document.querySelector('#c-empLev').value = '1';
        document.querySelector('#c-permSys').checked = false;
        document.querySelector('#c-permCpn').checked = false;
        document.querySelector('#c-permSta').checked = false;
        document.querySelector('input[name="c-actYn"][value="Y"]').checked = true;
        
        document.getElementById('createModal').style.display = 'flex';
    });

    // 3. 계정 저장 로직
    document.querySelector('#btn-save-user').addEventListener('click', () => {
        const empId = document.querySelector('#c-empId').value.trim();
        const empNm = document.querySelector('#c-empNm').value.trim();
        const depId = document.querySelector('#c-depId').value.trim();
        
        // 공통 Validation 연동
        if (!empId) return CommonUtils.toast('사용자ID를 입력하세요.', 'warning');
        if (!empNm) return CommonUtils.toast('사용자명을 입력하세요.', 'warning');
        if (!depId) return CommonUtils.toast('부서코드를 입력하세요.', 'warning');

        const empLev = document.querySelector('#c-empLev').value;
        const permSys = document.querySelector('#c-permSys').checked ? 'Y' : 'N';
        const permCpn = document.querySelector('#c-permCpn').checked ? 'Y' : 'N';
        const permSta = document.querySelector('#c-permSta').checked ? 'Y' : 'N';
        const actYn = document.querySelector('input[name="c-actYn"]:checked').value;

        // SMS.EMP 스키마에 맞는 JSON Payload
        const data = { 
            empId, empNm, depId, empLev, permSys, permCpn, permSta, actYn 
        };

        CommonUtils.confirm(`[${empNm}] 계정을 생성하시겠습니까?`, async () => {
            try {
                await axios.post('/api/user/create', data);
                CommonUtils.toast('계정이 성공적으로 생성되었습니다.', 'success');
                document.getElementById('createModal').style.display = 'none';
                pageBuilder.searchData(1); // TuiPageBuilder의 1페이지 재조회 자동 호출
            } catch (error) {
                // Interceptor에서 에러 토스트를 띄워줌
            }
        });
    });
});
