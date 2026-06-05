/**
 * 권한 관리 화면 전용 자바스크립트
 */
let grid;
let currentPage = 1;
let currentSize = 10;

document.addEventListener('DOMContentLoaded', function() {

    initGrid();
    bindEvents();
    searchData(1); // 화면 진입 시 최초 1회 자동 조회
});

/**
 * [그리드 초기화]
 * TUI Grid의 기본 옵션과 칼럼을 세팅합니다.
 */
function initGrid() {
    grid = new tui.Grid({
        el: document.getElementById('grid'),
        data: [],
        scrollX: TuiCommon.gridDefaults.scrollX,
        scrollY: TuiCommon.gridDefaults.scrollY,
        rowHeaders: ['checkbox', 'rowNum'], // 다중 선택 쳌박스 및 순번 표시
        columns: [
            { header: '권한코드', name: 'authCd', align: 'center', sortable: true },
            { header: '권한명', name: 'authNm', align: 'left', sortable: true },
            { 
                header: '사용여부', name: 'useYn', align: 'center', width: 100,
                formatter: ({ value }) => {
                    if (value === 'Y') return '<span class="badge badge-success">사용</span>';
                    if (value === 'N') return '<span class="badge badge-fail">미사용</span>';
                    return value || '-';
                }
            },
            { header: '등록자', name: 'regId', align: 'center', width: 120 },
            { header: '등록일시', name: 'regDt', align: 'center', width: 150, formatter: TuiCommon.fmt.date }
        ]
    });
}

/**
 * [데이터 조회]
 * 검색 조건과 페이징 정보를 서버(API)로 전송하여 권한 목록을 가져옵니다.
 * @param {number} page 조회할 페이지 번호 (기본값: 1)
 */
function searchData(page = 1) {
    currentPage = page;
    const authCode = document.getElementById('authCode').value;
    const authName = document.getElementById('authName').value;
    
    // 쿼리 파라미터 조합 (검색어 + 페이징)
    const params = new URLSearchParams({
        page: currentPage,
        size: currentSize,
        authCode: authCode,
        authName: authName
    });
    
    fetch(`${SERVER_DATA.apiUrl}?${params.toString()}`)
        .then(res => res.json())
        .then(resJson => {
            // 서버사이드 페이징 표준 규격(ApiResponse<PageResponseDTO>) 처리를 위한 파싱 로직
            if (resJson.code === 200) {
                const gridData = resJson.data.contents || [];
                grid.resetData(gridData); // TUI Grid에 데이터 밀어넣기
                
                // 공통 툴바 총 건수 갱신
                const totalCountEl = document.querySelector('.grid-toolbar .total-count strong');
                if (totalCountEl) {
                    totalCountEl.textContent = resJson.data.totalCount || 0;
                }

                // 외부 커스텀 페이지네이션 렌더링
                if(typeof TuiCommon !== 'undefined') {
                    TuiCommon.renderPagination(resJson.data.page || 1, resJson.data.totalPages || 1, searchData);
                }
            }
        })
        .catch(err => console.error('조회 중 통신 오류:', err));
}

/**
 * [이벤트 바인딩]
 * 화면 내의 각종 버튼 및 Select Box 이벤트를 등록합니다.
 */
function bindEvents() {
    // 1. 조회 버튼 이벤트 (항상 1페이지부터 조회)
    document.getElementById('btn-search')?.addEventListener('click', () => searchData(1));

    // 2. 초기화 버튼 이벤트 (입력칸 초기화 후 1페이지 조회)
    document.getElementById('btn-reset')?.addEventListener('click', function() {
        if(typeof CommonUtils !== 'undefined') {
            CommonUtils.resetFields();
        } else {
            document.getElementById('authCode').value = '';
            document.getElementById('authName').value = '';
        }
        searchData(1);
    });

    // 3. 페이지당 건수 콤보박스 변경 이벤트
    document.getElementById('pageSizeSelect')?.addEventListener('change', function(e) {
        currentSize = parseInt(e.target.value, 10);
        searchData(1); // 사이즈 변경 시 1페이지부터 다시 조회
    });

    // 3. 선택 삭제 버튼
    document.getElementById('btn-delete')?.addEventListener('click', function() {
        const checkedRows = grid.getCheckedRows();
        if (checkedRows.length === 0) {
            alert('삭제할 권한을 선택해주세요.');
            return;
        }

        if (confirm(`선택한 ${checkedRows.length}개 권한을 삭제하시겠습니까?`)) {
            const authCdList = checkedRows.map(row => row.authCd);
            
            fetch(SERVER_DATA.deleteUrl, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(authCdList)
            })
            .then(res => res.json())
            .then(res => {
                if (res.code === 200) {
                    alert(res.message || '삭제 완료되었습니다.');
                    searchData();
                }
            });
        }
    });
}
