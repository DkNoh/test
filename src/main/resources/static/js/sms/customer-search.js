document.addEventListener('DOMContentLoaded', function () {
    
    let currentPage = 1;
    let currentSize = 10;

    // ── Grid 초기화 ───────────────────────────────────────
    const grid = new tui.Grid({
        el:   document.querySelector('#grid'),
        data: [],
        ...TuiCommon.gridDefaults,
        columns: [
            { header: '발송일시',   name: 'sentAt',     width: 148, align: 'center', sortable: true,
              formatter: ({ value }) => TuiCommon.fmt.date(value) },
            { header: '고객 ID(수신)',name: 'receiverNo', width: 120, align: 'center' },
            { header: '발신번호',   name: 'senderNo',   width: 120, align: 'center' },
            { header: '메시지내용', name: 'message',    minWidth: 220,
              formatter: ({ value }) => value || '-' },
            { header: '발송상태',   name: 'sendStatus', width: 88,  align: 'center', sortable: true,
              formatter: TuiCommon.fmt.sendStatus },
            { header: '발송유형',   name: 'sendType',   width: 88,  align: 'center',
              formatter: TuiCommon.fmt.sendType },
        ],
    });

    function loadData() {
        const customerId = document.querySelector('#customerId').value.trim();
        const searchKeyword = document.querySelector('#searchKeyword').value.trim();
        const alertBox = document.querySelector('#validationAlert');
        
        alertBox.style.display = 'none';

        const requestBody = {
            customerId: customerId,
            searchKeyword: searchKeyword,
            page: currentPage,
            size: currentSize
        };

        axios.post(`${SERVER_DATA.apiUrl}/customer-search`, requestBody)
            .then(response => {
                grid.resetData(response.data.contents || []);
                TuiCommon.updateTotalCount(response.data.totalCount || 0);
                TuiCommon.renderPagination(response.data.page || 1, response.data.totalPages || 1, movePage);
            })
            .catch(error => {
                let errorMsg = error.response?.data?.message || '알 수 없는 오류가 발생했습니다.';
                
                alertBox.innerHTML = `<strong>규약 위반 에러:</strong><br>${errorMsg}`;
                alertBox.style.display = 'block';
                
                grid.resetData([]);
                TuiCommon.updateTotalCount(0);
                TuiCommon.renderPagination(1, 1, movePage);
            });
    }

    function movePage(page) { 
        currentPage = page; 
        loadData(); 
    }

    document.querySelector('#btn-search').addEventListener('click', () => { 
        currentPage = 1; 
        loadData(); 
    });

    document.querySelector('#btn-reset').addEventListener('click', () => { 
        document.querySelector('#customerId').value = '';
        document.querySelector('#searchKeyword').value = '';
        document.querySelector('#validationAlert').style.display = 'none';
        grid.resetData([]);
        TuiCommon.updateTotalCount(0);
        TuiCommon.renderPagination(1, 1, movePage);
    });

    document.querySelector('#pageSizeSelect').addEventListener('change', e => { 
        currentSize = +e.target.value; 
        currentPage = 1; 
        loadData(); 
    });

    document.querySelector('#customerId').addEventListener('keydown', e => { 
        if (e.key === 'Enter') { currentPage = 1; loadData(); } 
    });
    
    document.querySelector('#searchKeyword').addEventListener('keydown', e => { 
        if (e.key === 'Enter') { currentPage = 1; loadData(); } 
    });
});
