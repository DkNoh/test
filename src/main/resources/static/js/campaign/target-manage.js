document.addEventListener('DOMContentLoaded', function () {
    let currentPage = 1;
    let currentSize = 10;

    const dummyData = Array.from({ length: 12 }, (_, i) => ({
        targetId: `TGT${String(100+i).padStart(4,'0')}`,
        targetName: `마케팅 대상 ${i+1}`,
        count: (i+1) * 1500,
        regDate: CommonUtils.fmt ? CommonUtils.fmt.date(new Date(Date.now() - i * 3600000).toISOString()) : '-'
    }));

    const grid = new tui.Grid({
        el: document.querySelector('#grid'),
        data: [],
        ...TuiCommon.gridDefaults,
        columns: [
            { header: '대상ID', name: 'targetId', width: 150, align: 'center' },
            { header: '대상명', name: 'targetName', minWidth: 250, align: 'left' },
            { header: '대상건수', name: 'count', width: 150, align: 'right', formatter: ({value}) => Number(value).toLocaleString() },
            { header: '등록일자', name: 'regDate', width: 180, align: 'center' }
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
        document.querySelector('#keyword').value = '';
        grid.resetData([]);
        TuiCommon.updateTotalCount(0);
        TuiCommon.renderPagination(1, 1, movePage);
    });
    document.querySelector('#pageSizeSelect').addEventListener('change', e => {
        currentSize = +e.target.value; currentPage = 1; loadData();
    });

    loadData();
});
