document.addEventListener('DOMContentLoaded', function () {
    let currentPage = 1;
    let currentSize = 10;

    // ?”ë? ?°ì´???ì„± (API ?°ë™ ???”ë©´ ?•ì¸??
    const dummyData = Array.from({ length: 50 }, (_, i) => ({
        id: 50 - i,
        title: `[ê³µì?] ?œìŠ¤???ê? ?ˆë‚´ (${50 - i}ì°?`,
        author: i % 2 === 0 ? '?œìŠ¤?œê?ë¦¬ì' : '?´ì˜??,
        regDate: TuiCommon.fmt.date(new Date(Date.now() - i * 86400000).toISOString()),
        views: Math.floor(Math.random() * 100)
    }));

    const grid = new tui.Grid({
        el: document.querySelector('#grid'),
        data: [],
        ...TuiCommon.gridDefaults,
        columns: [
            { header: 'ë²ˆí˜¸', name: 'id', width: 80, align: 'center' },
            { header: '?œëª©', name: 'title', minWidth: 300 },
            { header: '?‘ì„±??, name: 'author', width: 120, align: 'center' },
            { header: '?±ë¡??, name: 'regDate', width: 150, align: 'center' },
            { header: 'ì¡°íšŒ??, name: 'views', width: 80, align: 'center' }
        ],
    });

    function loadData() {
        const start = (currentPage - 1) * currentSize;
        const end = start + currentSize;
        const pagedData = dummyData.slice(start, end);

        grid.resetData(pagedData);
        TuiCommon.updateTotalCount(dummyData.length);
        TuiCommon.renderPagination(currentPage, Math.ceil(dummyData.length / currentSize), movePage);
    }

    function movePage(page) {
        currentPage = page;
        loadData();
    }

    // ? ì§œ ê¸°ë³¸ê°??¸íŒ…
    CommonUtils.setDefaultDateTime();

    document.querySelector('#btn-search').addEventListener('click', () => {
        currentPage = 1;
        loadData();
    });

    document.querySelector('#btn-reset').addEventListener('click', () => {
        document.querySelector('#searchKeyword').value = '';
        CommonUtils.setDefaultDateTime();
        grid.resetData([]);
        TuiCommon.updateTotalCount(0);
        TuiCommon.renderPagination(1, 1, movePage);
    });

    document.querySelector('#pageSizeSelect').addEventListener('change', e => {
        currentSize = +e.target.value;
        currentPage = 1;
        loadData();
    });

    // ì§„ì… ???ë™ ì¡°íšŒ (? íƒ)
    loadData();
});
