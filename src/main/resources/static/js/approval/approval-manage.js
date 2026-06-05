/**
 * approval-manage.js
 * 결재 관리 화면의 프론트엔드 로직입니다.
 * TuiPageBuilder를 활용하여 공통 로직(그리드 렌더링, 페이징, 통신)은 숨기고,
 * 이 화면 고유의 특화된 비즈니스 로직(컬럼 정의, 모달 바인딩, 승인/반려 통신)만 작성되어 있습니다.
 */
document.addEventListener('DOMContentLoaded', function () {
    // 날짜 초기화는 TuiPageBuilder 내부에서 CommonUtils를 통해 자동으로 수행됩니다.

    // 2. Page Builder 구동 (그리드, 페이징, 통신, 이벤트 완전 자동화)
    // 인스턴스를 생성하는 즉시 내부적으로 TUI Grid가 렌더링되며 1페이지 조회가 자동 실행됩니다.
    const pageBuilder = new TuiPageBuilder({
        el: 'grid',                                                                 // 렌더링 대상 div ID
        apiUrl: '/approval/data',                                                   // 데이터 조회를 위한 REST API 엔드포인트
        searchInputs: ['startDate', 'endDate', 'approvalStatus', 'drafterName'],    // 해당 ID를 가진 입력값들을 자동으로 파라미터로 조합
        rowHeaders: ['checkbox', 'rowNum'],                                         // 행 좌측에 체크박스와 순번 표시
        columns: [
            { header: '결재 ID', name: 'aprvId', align: 'center', width: 150 },
            { header: '결재제목', name: 'reqTitle', align: 'left', minWidth: 200 },
            { header: '결재요청자', name: 'reqUserId', align: 'center' },
            { header: '요청일시', name: 'reqDt', align: 'center' },
            { header: '결재유형', name: 'aprvType', align: 'center', formatter: typeof TuiCommon !== 'undefined' ? TuiCommon.fmt.sendType : undefined },
            { 
                header: '상태', name: 'aprvStatus', align: 'center',
                // 결재 상태(PENDING, APPROVED, REJECTED)에 따라 CSS 배지를 동적으로 생성하는 커스텀 포매터
                formatter: ({ value }) => {
                    const map = { APPROVED: ['badge-success', '승인완료'], REJECTED: ['badge-fail', '반려'], PENDING: ['badge-wait', '결재대기'] };
                    const clsAndLabel = map[value] || ['', value || '-'];
                    return `<span class="badge ${clsAndLabel[0]}">${clsAndLabel[1]}</span>`;
                }
            },
            { header: '결재자', name: 'aprvUserId', align: 'center' },
            { header: '결재일시', name: 'aprvDt', align: 'center' }
        ],
        // 그리드 행을 더블클릭 했을 때 실행할 콜백 (행 데이터를 파라미터로 넘겨줌)
        onGridDblClick: (row) => openDetail(row)
    });

    // ── 상세 모달 열기 로직 (화면 특화) ──────────────────────────────────
    /**
     * 선택된 행(Row)의 데이터를 기반으로 결재 상세 모달 창을 띄웁니다.
     * @param {Object} row - TUI Grid의 선택된 행 데이터 객체
     */
    function openDetail(row) {
        if (!row) return; // 방어 로직
        
        const elId = document.getElementById('d-aprvId');
        if (!elId) {
            console.error("Detail modal HTML elements not found. Please refresh the page (Ctrl+F5).");
            return;
        }

        // 모달 창 내부의 텍스트 엘리먼트에 데이터를 바인딩합니다.
        elId.value = row.aprvId || '';
        document.getElementById('d-aprvId-text').textContent = row.aprvId || '-';
        document.getElementById('d-aprvType').textContent = row.aprvType || '-';
        document.getElementById('d-aprvStatus').textContent = row.aprvStatus || '-';
        document.getElementById('d-reqUserId').textContent = row.reqUserId || '-';
        document.getElementById('d-reqDt').textContent = row.reqDt || '-';
        document.getElementById('d-reqTitle').textContent = row.reqTitle || '-';

        // 대기(PENDING) 상태일 때만 '단건 승인', '단건 반려' 버튼을 화면에 노출시킵니다. (이미 처리된 건은 방어)
        const btnApprove = document.getElementById('btn-single-approve');
        const btnReject = document.getElementById('btn-single-reject');
        
        if (btnApprove && btnReject) {
            if (row.aprvStatus === 'PENDING') {
                btnApprove.style.display = 'inline-block';
                btnReject.style.display = 'inline-block';
            } else {
                btnApprove.style.display = 'none';
                btnReject.style.display = 'none';
            }
        }

        // 공통 유틸을 이용해 모달에 애니메이션 클래스를 주입하여 부드럽게 띄웁니다.
        if (typeof CommonUtils !== 'undefined') {
            document.getElementById('detailModal').classList.add('show');
        } else {
            const modal = document.getElementById('detailModal');
            if (modal) modal.style.display = 'flex';
        }
    }

    // ── 승인/반려 API 호출 로직 (화면 특화) ──────────────────────────────
    /**
     * 결재 ID 배열과 수행할 액션(승인/반려)을 서버로 전송합니다.
     * @param {Array<string>} aprvIds - 처리할 결재 ID들의 배열
     * @param {string} action - 'APPROVED' 또는 'REJECTED'
     */
    function processApproval(aprvIds, action) {
        if (!aprvIds || aprvIds.length === 0) return;
        
        const actionStr = action === 'APPROVED' ? '승인' : '반려';
        // 사용자에게 최종 확인을 받습니다.
        if (!confirm(`선택한 ${aprvIds.length}건을 ${actionStr} 처리하시겠습니까?`)) return;

        // 서버단 컨트롤러(@RequestBody List<ApprovalDTO>) 규격에 맞게 JSON 배열 형태로 페이로드를 조립합니다.
        const payload = aprvIds.map(id => ({ aprvId: id, aprvStatus: action }));

        // POST 비동기 요청을 수행합니다.
        fetch('/approval/process', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        })
        .then(res => res.json())
        .then(resJson => {
            // 통신 성공 및 비즈니스 로직 정상 처리 시
            if (resJson.code === 200) {
                alert(`${actionStr} 처리가 완료되었습니다.`);
                // 성공적으로 처리되었으므로 열려있는 모달을 닫습니다.
                if (typeof CommonUtils !== 'undefined') {
                    CommonUtils.closeModal();
                } else {
                    document.getElementById('detailModal').style.display = 'none';
                }
                // 처리가 끝난 뒤, PageBuilder의 메서드를 호출하여 현재 보던 페이지를 다시 새로고침(조회)합니다.
                pageBuilder.searchData(pageBuilder.getCurrentPage()); 
            } else {
                // 비즈니스 로직 처리 실패 시 서버 에러 메시지 노출
                alert(resJson.message || `${actionStr} 처리에 실패했습니다.`);
            }
        })
        .catch(err => {
            console.error(err);
            alert(`서버 통신 중 오류가 발생했습니다.`);
        });
    }

    // ─────────────────────────────────────────────────────────────
    // 이벤트 리스너 바인딩 구역 (화면에 특화된 버튼들의 클릭 이벤트)
    // ─────────────────────────────────────────────────────────────

    // [모달 내부] 단건 승인 버튼 클릭 시
    document.getElementById('btn-single-approve')?.addEventListener('click', () => {
        // 모달 안의 hidden input에 저장해둔 결재 ID를 꺼내어 서버로 넘깁니다.
        processApproval([document.getElementById('d-aprvId').value], 'APPROVED');
    });

    // [모달 내부] 단건 반려 버튼 클릭 시
    document.getElementById('btn-single-reject')?.addEventListener('click', () => {
        processApproval([document.getElementById('d-aprvId').value], 'REJECTED');
    });

    // [툴바 메인] 일괄 승인 버튼 클릭 시
    document.querySelector('#btn-approve')?.addEventListener('click', () => {
        // 체크된 행 중에서 'PENDING(대기)' 상태인 항목들만 필터링합니다. (이미 승인된 것 중복 방지)
        const checkedRows = pageBuilder.getCheckedRows().filter(r => r.aprvStatus === 'PENDING');
        if (checkedRows.length === 0) {
            alert('승인 가능한(대기 상태) 항목을 선택해주세요.');
            return;
        }
        // 추출된 객체들에서 aprvId만 뽑아 문자열 배열로 만들어 서버에 던집니다.
        processApproval(checkedRows.map(r => r.aprvId), 'APPROVED');
    });

    // [툴바 메인] 일괄 반려 버튼 클릭 시
    document.querySelector('#btn-reject')?.addEventListener('click', () => {
        const checkedRows = pageBuilder.getCheckedRows().filter(r => r.aprvStatus === 'PENDING');
        if (checkedRows.length === 0) {
            alert('반려 가능한(대기 상태) 항목을 선택해주세요.');
            return;
        }
        processApproval(checkedRows.map(r => r.aprvId), 'REJECTED');
    });
});
