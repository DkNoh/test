document.addEventListener('DOMContentLoaded', function () {
    let currentPage = 1;
    let currentSize = 10;

    const dummyData = Array.from({ length: 15 }, (_, i) => ({
        requestDate: CommonUtils.fmt ? CommonUtils.fmt.date(new Date(Date.now() - i * 86400000).toISOString()) : '-',
        phone: `010-1234-${String(1000 + i)}`,
        status: i % 3 === 0 ? '처리완료' : '접수',
        processedDate: i % 3 === 0 ? (CommonUtils.fmt ? CommonUtils.fmt.date(new Date().toISOString()) : '-') : '-'
    }));

    const grid = new tui.Grid({
        el: document.querySelector('#grid'),
        data: [],
        ...TuiCommon.gridDefaults,
        columns: [
            { header: '요청일자', name: 'requestDate', width: 150, align: 'center' },
            { header: '전화번호', name: 'phone', width: 150, align: 'center' },
            { header: '처리상태', name: 'status', width: 100, align: 'center' },
            { header: '처리일자', name: 'processedDate', width: 150, align: 'center' }
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
        document.querySelector('#phone').value = '';
        document.querySelector('#statusSelect').value = '';
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
