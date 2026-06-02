/**
 * dept-stat-manage.js
 * 부서별 발송 통계 화면 스크립트
 */
document.addEventListener('DOMContentLoaded', function () {
    const pageBuilder = new TuiPageBuilder({
        el: 'grid',
        apiUrl: '/sms/dept-stat/data',
        searchInputs: ['startDate', 'endDate', 'deptNm'],
        rowHeaders: ['rowNum'],
        columns: [
            { header: 'DEPT_ID', name: 'deptId', align: 'center', width: 150 },
            { header: 'DEPT_NM', name: 'deptNm', align: 'center', width: 150 },
            { header: 'TOTAL_SEND_CNT', name: 'totalSendCnt', align: 'center', width: 150 },
            { header: 'TOTAL_SUCCESS_CNT', name: 'totalSuccessCnt', align: 'center', width: 150 },
            { header: 'TOTAL_FAIL_CNT', name: 'totalFailCnt', align: 'center', width: 150 }
        ],
        autoModal: true,
        autoModalTitle: '부서별 발송 통계 상세'
    });

    // 신규 등록 버튼 이벤트 예시
    document.getElementById('btn-create')?.addEventListener('click', () => {
        alert('신규 등록 팝업 구현');
    });

    // 대용량 엑셀 다운로드 이벤트 (Backend)
    document.getElementById('btn-excel')?.addEventListener('click', () => {
        const qs = `?startDate=${document.getElementById('startDate').value}&endDate=${document.getElementById('endDate').value}&deptNm=${document.getElementById('deptNm').value}`;
        window.location.href = pageBuilder.config.apiUrl.replace('/data', '') + '/excel' + qs;
    });

    // 현재 화면 엑셀 다운로드 이벤트 (TUI Grid Native)
    document.getElementById('btn-excel-grid')?.addEventListener('click', () => {
        pageBuilder.getGrid().export('xlsx', { fileName: '부서별 발송 통계_화면데이터' });
    });
});
