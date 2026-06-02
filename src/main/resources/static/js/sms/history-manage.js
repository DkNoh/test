/**
 * history-manage.js
 * 메시지 발송 이력 조회2 화면 스크립트
 */
document.addEventListener('DOMContentLoaded', function () {
    const pageBuilder = new TuiPageBuilder({
        el: 'grid',
        apiUrl: '/sms/history/data',
        searchInputs: ['startDate', 'endDate', 'searchName', 'status'],
        rowHeaders: ['rowNum'],
        columns: [
            { header: 'msgId', name: 'msgid', align: 'center', width: 150 },
            { header: 'senderId', name: 'senderid', align: 'center', width: 150 },
            { header: 'senderName', name: 'sendername', align: 'center', width: 150 },
            { header: 'receiverPhone', name: 'receiverphone', align: 'center', width: 150 },
            { header: 'msgType', name: 'msgtype', align: 'center', width: 150 },
            { header: 'sendStatus', name: 'sendstatus', align: 'center', width: 150 },
            { header: 'sendDt', name: 'senddt', align: 'center', width: 150 }
        ],
        autoModal: true,
        autoModalTitle: '메시지 발송 이력 조회2 상세'
    });

    // 신규 등록 버튼 이벤트 예시
    document.getElementById('btn-create')?.addEventListener('click', () => {
        alert('신규 등록 팝업 구현');
    });
});
