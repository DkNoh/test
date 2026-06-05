document.addEventListener('DOMContentLoaded', function () {
    new TuiPageBuilder({
        el: 'grid',
        apiUrl: '/sms/campaign-lms/data',
        searchInputs: ['startDate', 'endDate', 'campaignName', 'regId'],
        rowHeaders: ['rowNum'],
        columns: [
            { header: '캠페인명', name: 'campaignName', minWidth: 200, align: 'left' },
            { header: '등록ID',   name: 'regId',        width: 120,   align: 'center' },
            { header: '전송일시', name: 'sentAt',        width: 160,   align: 'center' },
            { header: '내용',     name: 'message',       minWidth: 200, align: 'left' },
            { header: '발송건수', name: 'sendCount',     width: 90,    align: 'center' },
            { header: '상태',     name: 'status',        width: 90,    align: 'center' }
        ]
    });
});
