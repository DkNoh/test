/**
 * menu-manage.js
 * 메뉴 및 권한 관리 (Master-Detail 구조)
 */
document.addEventListener('DOMContentLoaded', function () {
    
    // TuiPageBuilder를 사용하되, 자동 팝업(autoModal)은 끄고 커스텀 클릭 이벤트를 연동합니다.
    const pageBuilder = new TuiPageBuilder({
        el: 'grid',
        apiUrl: '/system/menu-manage/data',
        searchInputs: ['searchKeyword', 'useYn'],
        rowHeaders: ['rowNum'],
        columns: [
            { header: '메뉴코드', name: 'menuCd', align: 'center', width: 120 },
            { header: '메뉴명', name: 'menuNm', minWidth: 150 },
            { header: 'URL', name: 'menuUrl', minWidth: 150 },
            { header: '상위코드', name: 'upMenuCd', align: 'center', width: 100 },
            { header: '정렬', name: 'sortOrd', align: 'center', width: 60 },
            { header: '사용', name: 'useYn', align: 'center', width: 60 },
            { header: '권한', name: 'authNames', minWidth: 200 }
        ]
    });

    const grid = pageBuilder.grid;

    // 우측 폼 요소들
    const formEls = {
        mode: document.getElementById('editMode'),
        menuCd: document.getElementById('menuCd'),
        upMenuCd: document.getElementById('upMenuCd'),
        menuNm: document.getElementById('menuNm'),
        menuUrl: document.getElementById('menuUrl'),
        sortOrd: document.getElementById('sortOrd'),
        useYn: document.getElementById('dtlUseYn')
    };

    const btnDelete = document.getElementById('btn-delete');
    const authCheckboxes = document.querySelectorAll('input[name="authRoles"]');
    const actionCheckboxes = document.querySelectorAll('input[name="authAction"]');

    // 폼 초기화 함수
    function resetForm() {
        formEls.mode.value = "1"; // 신규
        formEls.menuCd.value = "";
        formEls.menuCd.readOnly = false; // 신규일 땐 입력 가능
        formEls.upMenuCd.value = "";
        formEls.menuNm.value = "";
        formEls.menuUrl.value = "";
        formEls.sortOrd.value = "1";
        formEls.useYn.value = "Y";
        
        authCheckboxes.forEach(cb => cb.checked = false);
        setDefaultActionPermissions();
        btnDelete.style.display = 'none';
    }

    function setDefaultActionPermissions() {
        const defaults = {
            ROLE_ADMIN: { canRead: true, canWrite: true, canApprove: true, canExcel: true },
            ROLE_MANAGER: { canRead: true, canWrite: true, canApprove: false, canExcel: true },
            ROLE_USER: { canRead: true, canWrite: false, canApprove: false, canExcel: false },
            ROLE_VIEWER: { canRead: true, canWrite: false, canApprove: false, canExcel: true }
        };

        actionCheckboxes.forEach(cb => {
            const role = cb.dataset.role;
            const action = cb.dataset.action;
            cb.checked = Boolean(defaults[role] && defaults[role][action]);
        });
    }

    function setActionRowEnabled(role, enabled) {
        actionCheckboxes.forEach(cb => {
            if (cb.dataset.role === role) {
                cb.disabled = !enabled;
            }
        });
    }

    authCheckboxes.forEach(cb => {
        cb.addEventListener('change', () => {
            setActionRowEnabled(cb.value, cb.checked);
        });
        setActionRowEnabled(cb.value, cb.checked);
    });

    // 그리드 행 클릭 이벤트 -> 상세 정보 로드
    grid.on('click', (ev) => {
        if (ev.rowKey == null) return;
        const row = grid.getRow(ev.rowKey);
        
        // 폼 채우기
        formEls.mode.value = "2"; // 수정
        formEls.menuCd.value = row.menuCd;
        formEls.menuCd.readOnly = true; // 수정일 땐 PK 변경 불가
        formEls.upMenuCd.value = row.upMenuCd || "";
        formEls.menuNm.value = row.menuNm || "";
        formEls.menuUrl.value = row.menuUrl || "";
        formEls.sortOrd.value = row.sortOrd || 1;
        formEls.useYn.value = row.useYn || "Y";
        btnDelete.style.display = 'block';

        // 해당 메뉴의 권한 데이터 서버에서 불러오기
        axios.get(`/system/menu-manage/auth-data?menuCd=${row.menuCd}`)
            .then(res => {
                const permissions = res.data || [];
                const roles = permissions.map(auth => auth.authCd);
                authCheckboxes.forEach(cb => {
                    cb.checked = roles.includes(cb.value);
                    setActionRowEnabled(cb.value, cb.checked);
                });
                setDefaultActionPermissions();
                permissions.forEach(auth => {
                    actionCheckboxes.forEach(cb => {
                        if (cb.dataset.role === auth.authCd) {
                            cb.checked = auth[cb.dataset.action] === 'Y';
                        }
                    });
                });
            });
    });

    // 신규 메뉴 버튼
    document.getElementById('btn-new').addEventListener('click', resetForm);
    document.getElementById('btn-cancel').addEventListener('click', resetForm);

    // 저장 버튼
    document.getElementById('btn-save').addEventListener('click', () => {
        if (!formEls.menuCd.value.trim()) return alert('메뉴 코드를 입력하세요.');
        if (!formEls.menuNm.value.trim()) return alert('메뉴 명을 입력하세요.');

        const checkedRoles = Array.from(authCheckboxes)
            .filter(cb => cb.checked)
            .map(cb => cb.value);

        const authPermissions = checkedRoles.map(role => {
            const permission = {
                authCd: role,
                canRead: 'N',
                canWrite: 'N',
                canApprove: 'N',
                canExcel: 'N'
            };
            actionCheckboxes.forEach(cb => {
                if (cb.dataset.role === role) {
                    permission[cb.dataset.action] = cb.checked ? 'Y' : 'N';
                }
            });
            return permission;
        });

        const payload = {
            mode: formEls.mode.value,
            menu: {
                menuCd: formEls.menuCd.value,
                upMenuCd: formEls.upMenuCd.value,
                menuNm: formEls.menuNm.value,
                menuUrl: formEls.menuUrl.value,
                sortOrd: parseInt(formEls.sortOrd.value || 1, 10),
                useYn: formEls.useYn.value
            },
            authRoles: checkedRoles,
            authPermissions: authPermissions
        };

        if (confirm('저장하시겠습니까?')) {
            axios.post('/system/menu-manage/save', payload)
                .then(res => {
                    alert(res.data.message || '저장되었습니다.');
                    pageBuilder.searchData(pageBuilder.currentPage); // 그리드 새로고침
                    resetForm();
                });
        }
    });

    // 삭제 버튼
    btnDelete.addEventListener('click', () => {
        const menuCd = formEls.menuCd.value;
        if (!menuCd) return;

        if (confirm(`메뉴 [${menuCd}]를 삭제하시겠습니까?\n매핑된 권한 정보도 모두 삭제됩니다.`)) {
            axios.post(`/system/menu-manage/delete?menuCd=${menuCd}`)
                .then(res => {
                    alert(res.data.message || '삭제되었습니다.');
                    pageBuilder.searchData(1); // 1페이지로 새로고침
                    resetForm();
                });
        }
    });
});
