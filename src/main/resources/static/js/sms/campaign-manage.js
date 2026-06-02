/**
 * campaign-manage.js
 * 데모 화면 화면 스크립트
 */
document.addEventListener('DOMContentLoaded', function () {
    const pageBuilder = new TuiPageBuilder({
        el: 'grid',
        apiUrl: '/sms/campaign/data',
        searchInputs: ['SEND_TYPE'],
        rowHeaders: ['rowNum'],
        columns: [
            { header: 'CAMPAIGN_ID', name: 'campaignId', align: 'center', width: 150 },
            { header: 'CAMPAIGN_NAME', name: 'campaignName', align: 'center', width: 150 },
            { header: 'SEND_TYPE', name: 'sendType', align: 'center', width: 150 },
            { header: 'STATUS', name: 'status', align: 'center', width: 150 },
            { header: 'TOTAL_CNT', name: 'totalCnt', align: 'center', width: 150 },
            { header: 'SUCCESS_CNT', name: 'successCnt', align: 'center', width: 150 },
            { header: 'FAIL_CNT', name: 'failCnt', align: 'center', width: 150 },
            { header: 'PENDING_CNT', name: 'pendingCnt', align: 'center', width: 150 },
            { header: 'SUCCESS_RATE', name: 'successRate', align: 'center', width: 150 }
        ],
        autoModal: true,
        autoModalTitle: '데모 화면 상세'
    });

    // 신규 등록 버튼 이벤트 예시
    document.getElementById('btn-create')?.addEventListener('click', () => {
        alert('신규 등록 팝업 구현');
    });
});
