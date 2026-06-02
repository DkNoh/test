/**
 * group-manage.js
 * 주소록 마스터-디테일 화면 스크립트 (좌: 그룹, 우: 연락처)
 */
document.addEventListener('DOMContentLoaded', function () {
    
    // ----------------------------------------------------
    // [Master] 주소록 그룹 그리드 초기화
    // ----------------------------------------------------
    const groupBuilder = new TuiPageBuilder({
        el: 'grid-group',
        apiUrl: '/contact/group/data',
        searchInputs: ['searchKeyword'],
        rowHeaders: ['rowNum'],
        columns: [
            { header: '그룹 ID', name: 'groupId', align: 'center', width: 80 },
            { header: '그룹명', name: 'groupNm', align: 'center', width: 150 },
            { header: '설명', name: 'groupDesc', align: 'left', minWidth: 200 },
            { header: '사용여부', name: 'useYn', align: 'center', width: 80 },
            { header: '등록일시', name: 'regDt', align: 'center', width: 150 }
        ],
        btnSearch: 'btn-search',
        btnReset: 'btn-reset',
        pageSizeEl: 'pageSizeSelect',
        paginationId: 'pagination-group',
        totalCountSelector: '.split-left .total-count strong'
    });

    // ----------------------------------------------------
    // [Detail] 연락처 그리드 초기화
    // ----------------------------------------------------
    const contactBuilder = new TuiPageBuilder({
        el: 'grid-contact',
        apiUrl: '/contact/item/data',
        searchInputs: ['groupId'], // 숨겨진 input에서 groupId 읽기
        rowHeaders: ['rowNum'],
        columns: [
            { header: '연락처명', name: 'contactNm', align: 'center', width: 120 },
            { header: '전화번호', name: 'phoneNo', align: 'center', width: 130 },
            { header: '회사명', name: 'companyNm', align: 'left', minWidth: 150 },
            { header: '등록일시', name: 'regDt', align: 'center', width: 150 }
        ],
        btnSearch: null, // 우측은 조회 버튼 종속 없음
        btnReset: null,
        pageSizeEl: 'contactPageSizeSelect',
        paginationId: 'pagination-contact',
        totalCountSelector: '#contact-total-count strong'
    });

    // ----------------------------------------------------
    // [Event] 그룹 목록 클릭 시 연락처 목록 조회
    // ----------------------------------------------------
    groupBuilder.grid.on('click', (ev) => {
        if (ev.rowKey !== null && ev.rowKey !== undefined) {
            const row = groupBuilder.grid.getRow(ev.rowKey);
            
            // 선택된 행 하이라이트 처리는 TUI 기본 지원이지만 시각적 강화를 위해 선택될 수 있음
            document.getElementById('groupId').value = row.groupId;
            
            // 상세 그리드 제목 변경 (옵션)
            // document.getElementById('contact-total-count').parentNode.innerHTML = `[${row.groupNm}] 연락처 총 <strong id="contact-total-count">0</strong>건`;
            
            // 연락처 조회 호출
            contactBuilder.searchData(1);
            
            // 버튼 활성화
            document.getElementById('btn-create-contact').style.display = 'inline-block';
        }
    });

    // 버튼 이벤트들
    document.getElementById('btn-create-group')?.addEventListener('click', () => {
        alert('주소록 그룹 등록 팝업 띄우기');
    });

    document.getElementById('btn-create-contact')?.addEventListener('click', () => {
        const gid = document.getElementById('groupId').value;
        if (!gid) {
            alert('먼저 좌측에서 그룹을 선택해주세요.');
            return;
        }
        alert(`그룹 ID [${gid}]에 연락처 추가 팝업 띄우기`);
    });

    // 대용량 엑셀 다운로드 (마스터)
    document.getElementById('btn-excel')?.addEventListener('click', () => {
        const qs = `?searchKeyword=${document.getElementById('searchKeyword').value}`;
        window.location.href = '/contact/group/excel' + qs;
    });

    document.getElementById('btn-excel-grid')?.addEventListener('click', () => {
        groupBuilder.grid.export('xlsx', { fileName: '주소록그룹_화면데이터' });
    });
});