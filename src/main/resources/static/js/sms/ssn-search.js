document.addEventListener('DOMContentLoaded', function () {
    let currentPage = 1;
    let currentSize = 10;

    const dummyData = Array.from({ length: 20 }, (_, i) => ({
        sentAt: CommonUtils.fmt ? CommonUtils.fmt.date(new Date(Date.now() - i * 3600000).toISOString()) : '-',
        receiverNo: `010-9999-${String(1000 + i)}`,
        senderNo: '1588-0000',
        content: `[주민번호 포함] 안녕하세요 테스트 ${i}입니다.`,
        sendStatus: i % 4 === 0 ? 'FAIL' : 'SUCCESS',
        sendType: i % 2 === 0 ? 'SMS' : 'LMS'
    }));

    const grid = new tui.Grid({
        el: document.querySelector('#grid'),
        data: [],
        ...TuiCommon.gridDefaults,
        columns: [
            { header: '발송일시', name: 'sentAt', width: 150, align: 'center' },
            { header: '수신번호', name: 'receiverNo', width: 120, align: 'center' },
            { header: '발신번호', name: 'senderNo', width: 120, align: 'center' },
            { header: '메시지', name: 'content', minWidth: 200, align: 'left' },
            { header: '발송상태', name: 'sendStatus', width: 100, align: 'center', formatter: TuiCommon.fmt.sendStatus },
            { header: '발송유형', name: 'sendType', width: 100, align: 'center', formatter: TuiCommon.fmt.sendType }
        ],
    });

    function loadData() {
        const start = (currentPage - 1) * currentSize;
        const end = start + currentSize;
        grid.resetData(dummyData.slice(start, end));
        TuiCommon.updateTotalCount(dummyData.length);
        TuiCommon.renderPagination(currentPage, Math.ceil(dummyData.length / currentSize), movePage);
    }

    function movePage(page) { currentPage = page; loadData(); }

    document.querySelector('#btn-search').addEventListener('click', () => { currentPage = 1; loadData(); });
    document.querySelector('#btn-reset').addEventListener('click', () => {
        document.querySelector('#receiverNo').value = '';
        CommonUtils.setDefaultDateTime();
        grid.resetData([]);
        TuiCommon.updateTotalCount(0);
        TuiCommon.renderPagination(1, 1, movePage);
    });
    document.querySelector('#pageSizeSelect').addEventListener('change', e => {
        currentSize = +e.target.value; currentPage = 1; loadData();
    });

    CommonUtils.setDefaultDateTime();
});
