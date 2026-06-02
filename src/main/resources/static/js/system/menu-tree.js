document.addEventListener('DOMContentLoaded', async function () {
    // 1. TUI Grid 초기화 (Tree 모드 활성화)
    const grid = new tui.Grid({
        el: document.getElementById('tree-grid'),
        data: [], // 초기엔 빈 배열, 하단 API에서 로드
        rowHeaders: ['rowNum'],
        bodyHeight: 650, // 모던 UI에 맞춰 높이 증가
        treeColumnOptions: {
            name: 'menuNm', // 트리의 깊이가 렌더링될 컬럼
            useCascadingCheckbox: false
        },
        columns: [
            { header: '메뉴명', name: 'menuNm', minWidth: 250 },
            { header: '메뉴 코드', name: 'menuCd', align: 'center', width: 120 },
            { header: '상위 코드', name: 'upMenuCd', align: 'center', width: 100 },
            { header: '메뉴 URL', name: 'menuUrl', width: 200 },
            { header: '순서', name: 'sortOrd', align: 'center', width: 60 },
            { header: '사용', name: 'useYn', align: 'center', width: 60 }
        ]
    });

    tui.Grid.applyTheme('clean'); // 깔끔한 테마 적용

    // 2. 폼 제어용 DOM 변수
    const form = {
        editMode: document.getElementById('editMode'),
        menuCd: document.getElementById('menuCd'),
        upMenuCd: document.getElementById('upMenuCd'),
        menuNm: document.getElementById('menuNm'),
        menuUrl: document.getElementById('menuUrl'),
        sortOrd: document.getElementById('sortOrd'),
        useYn: document.getElementById('dtlUseYn')
    };

    // 폼 초기화 (신규 모드)
    function resetForm(parentCd = '') {
        form.editMode.value = '1';
        form.menuCd.value = '';
        form.menuCd.readOnly = false;
        form.upMenuCd.value = parentCd; // 선택된 노드가 있다면 그 자식으로 자동 입력
        form.menuNm.value = '';
        form.menuUrl.value = '';
        form.sortOrd.value = '1';
        form.useYn.value = 'Y';
        document.querySelectorAll('input[name="authRoles"]').forEach(cb => cb.checked = false);
    }

    // 트리 데이터 로드 (Axios + Spinner + Interceptor 적용)
    async function loadTreeData() {
        try {
            const res = await axios.get('/system/menu-manage/tree-data');
            // common-utils의 interceptor가 성공 시 데이터를 자동으로 언래핑(res.data)하여 전달
            if (res.data && res.data.length > 0) {
                grid.resetData(res.data);
                grid.expandAll(); // 최초 로드시 모두 펼치기
            }
        } catch (error) {
            // interceptor가 에러 모달을 띄우므로 콘솔 로깅만 수행
            console.error('Tree fetch error:', error);
        }
        
        // CSS 리플로우 후 그리드 깨짐 방지
        setTimeout(() => grid.refreshLayout(), 100);
    }

    loadTreeData();

    // 3. 트리 행(Row) 클릭 시 우측 상세 폼에 바인딩
    grid.on('click', async (ev) => {
        if (ev.rowKey === undefined) return; 
        const row = grid.getRow(ev.rowKey);
        if (!row) return;

        form.editMode.value = '2'; // 수정 모드 전환
        form.menuCd.value = row.menuCd || '';
        form.menuCd.readOnly = true; // 수정 시 Key 값인 메뉴코드는 변경 불가
        form.upMenuCd.value = row.upMenuCd || '';
        form.menuNm.value = row.menuNm || '';
        form.menuUrl.value = row.menuUrl || '';
        form.sortOrd.value = row.sortOrd || '1';
        form.useYn.value = row.useYn || 'Y';

        // 권한 정보 비동기 로드
        document.querySelectorAll('input[name="authRoles"]').forEach(cb => cb.checked = false);
        try {
            const res = await axios.get(`/system/menu-manage/auth-data?menuCd=${row.menuCd}`);
            if (res.data) {
                res.data.forEach(role => {
                    const cb = document.querySelector(`input[name="authRoles"][value="${role}"]`);
                    if (cb) cb.checked = true;
                });
            }
        } catch (e) {
            console.error(e);
        }
    });

    // 4. 로직 - 트리 행 추가 (가상의 새 행 삽입)
    function addTreeRowLogic(isSibling) {
        const focused = grid.getFocusedCell();
        let targetParentRowKey = null;
        let parentCd = '';
        let offset = undefined;

        const allData = grid.getData();

        if (focused && focused.rowKey !== null && focused.rowKey !== undefined) {
            const row = grid.getRow(focused.rowKey);
            
            if (isSibling) {
                // 동급(형제) 추가
                const parentRow = allData.find(r => r.menuCd === row.upMenuCd);
                targetParentRowKey = parentRow ? parentRow.rowKey : null;
                parentCd = row.upMenuCd || '';

                const siblings = allData.filter(r => (r.upMenuCd || '') === parentCd);
                const index = siblings.findIndex(r => r.rowKey === row.rowKey);
                if (index !== -1) offset = index + 1;
            } else {
                // 하위(자식) 추가
                targetParentRowKey = row.rowKey;
                parentCd = row.menuCd;
            }
        } else if (!isSibling) {
            CommonUtils.toast('상위 메뉴를 먼저 선택한 후 하위 메뉴를 추가해주세요.', 'warning');
            return;
        }

        const newRow = {
            menuNm: '[새 메뉴]',
            menuCd: '',
            upMenuCd: parentCd,
            menuUrl: '',
            sortOrd: 1,
            useYn: 'Y'
        };
        
        const options = { focus: true };
        if (targetParentRowKey !== null) options.parentRowKey = targetParentRowKey;
        if (offset !== undefined) options.offset = offset;
        
        try {
            grid.appendTreeRow(newRow, options);
            if (targetParentRowKey !== null) grid.expand(targetParentRowKey);
            
            // 새 행으로 포커스
            setTimeout(() => {
                const allDataAfter = grid.getData();
                const addedRows = allDataAfter.filter(r => r.menuNm === '[새 메뉴]' && r.menuCd === '');
                if (addedRows.length > 0) {
                    const lastAdded = addedRows[addedRows.length - 1];
                    const rowIndex = grid.getIndexOfRow(lastAdded.rowKey);
                    if (rowIndex > -1) grid.focusAt(rowIndex, 0, true);
                }
            }, 50);
        } catch(e) {
            console.error('Tree append error: ', e);
            CommonUtils.toast('트리 렌더링 중 오류가 발생했습니다.', 'error');
        }

        resetForm(parentCd);
        form.menuNm.value = '[새 메뉴]';
        form.menuCd.focus();
    }

    // [버튼 바인딩]
    document.getElementById('btn-add-child').addEventListener('click', () => addTreeRowLogic(false));
    document.getElementById('btn-add-sibling').addEventListener('click', () => addTreeRowLogic(true));

    document.getElementById('btn-delete-node').addEventListener('click', () => {
        const focused = grid.getFocusedCell();
        if (focused === null || focused.rowKey === null || focused.rowKey === undefined) {
            CommonUtils.toast('삭제할 메뉴를 선택해주세요.', 'warning');
            return;
        }
        
        const row = grid.getRow(focused.rowKey);
        
        // 공통 커스텀 모달 활용
        CommonUtils.confirm(`[${row.menuNm}] 메뉴를 정말 삭제하시겠습니까?\n하위 메뉴가 있을 경우 삭제되지 않거나 오류가 발생할 수 있습니다.`, async () => {
            try {
                const params = new URLSearchParams();
                params.append('menuCd', row.menuCd);

                const res = await axios.post('/system/menu-manage/delete', params);
                CommonUtils.toast('정상적으로 삭제되었습니다.', 'success');
                loadTreeData(); // 무식한 location.reload() 대신 SPA 방식의 데이터 리로드 적용
                resetForm('');
            } catch (e) {
                // interceptor handles the error alert
            }
        }, '메뉴 삭제 확인');
    });

    document.getElementById('btn-save').addEventListener('click', async () => {
        if (!form.menuCd.value || !form.menuNm.value) {
            CommonUtils.toast('메뉴 코드와 메뉴 명은 필수 입력 항목입니다.', 'warning');
            form.menuCd.focus();
            return;
        }

        const authRoles = [];
        document.querySelectorAll('input[name="authRoles"]:checked').forEach(cb => {
            authRoles.push(cb.value);
        });

        const reqData = {
            mode: form.editMode.value,
            menu: {
                menuCd: form.menuCd.value,
                upMenuCd: form.upMenuCd.value,
                menuNm: form.menuNm.value,
                menuUrl: form.menuUrl.value,
                sortOrd: parseInt(form.sortOrd.value || '1'),
                useYn: form.useYn.value
            },
            authRoles: authRoles
        };

        try {
            const res = await axios.post('/system/menu-manage/save', reqData);
            CommonUtils.toast('성공적으로 저장되었습니다.', 'success');
            loadTreeData(); // 화면 껌뻑임 없는 데이터 갱신
            
            // 저장 완료 후 수정 모드로 강제 고정
            form.editMode.value = '2';
            form.menuCd.readOnly = true;
        } catch (e) {
            // interceptor handles error
        }
    });
});
