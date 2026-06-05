document.addEventListener('DOMContentLoaded', function () {

    new TuiPageBuilder({
        el: 'grid',
        apiUrl: '/campaign/target-manage/data',
        searchInputs: [],
        rowHeaders: ['rowNum'],
        columns: [
            { header: '캠페인ID',   name: 'campaignId',   width: 110, align: 'center' },
            { header: '발송대상명', name: 'campaignName', minWidth: 200, align: 'left' },
            { header: '등록일시',   name: 'regDt',        width: 160, align: 'center' },
            { header: '등록자',     name: 'regId',        width: 100, align: 'center' },
            { header: '대상수',     name: 'targetCount',  width: 90,  align: 'center' },
            { header: '발송횟수',   name: 'sendCount',    width: 90,  align: 'center' },
            { header: '승인여부',   name: 'status',       width: 90,  align: 'center' },
            { header: '승인자',     name: 'aprvUser',     width: 100, align: 'center' }
        ]
    });
});
