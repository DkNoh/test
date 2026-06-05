document.addEventListener('DOMContentLoaded', function () {
    const today = new Date().toISOString().split('T')[0];
    document.querySelector('#searchDate').value = today;

    const fmt = value => (value ?? 0).toLocaleString();
    const rate = (success, total) => total ? `${(success / total * 100).toFixed(1)}%` : '0.0%';

    new TuiPageBuilder({
        el: 'grid',
        apiUrl: '/statistics/dept-send-stat/data',
        searchInputs: ['searchDate', 'deptId'],
        rowHeaders: ['rowNum'],
        columns: [
            { header: '부서코드', name: 'deptId', width: 120, align: 'center' },
            { header: '부서명', name: 'deptNm', minWidth: 160, align: 'left' },
            { header: '전송중', name: 'sendingCount', width: 100, align: 'right', formatter: ({ value }) => fmt(value) },
            { header: '실패', name: 'failCount', width: 100, align: 'right', formatter: ({ value }) => fmt(value) },
            { header: '성공', name: 'successCount', width: 100, align: 'right', formatter: ({ value }) => fmt(value) },
            { header: '총건수', name: 'totalCount', width: 110, align: 'right', formatter: ({ value }) => fmt(value) },
            {
                header: '성공률',
                name: 'successRate',
                width: 100,
                align: 'center',
                formatter: ({ row }) => rate(row.successCount || 0, row.totalCount || 0)
            }
        ]
    });

    document.querySelector('#btn-reset')?.addEventListener('click', () => {
        document.querySelector('#searchDate').value = today;
        document.querySelector('#deptId').value = '';
    });
});
