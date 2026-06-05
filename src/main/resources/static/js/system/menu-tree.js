document.addEventListener('DOMContentLoaded', async function () {
    const grid = new tui.Grid({
        el: document.getElementById('tree-grid'),
        data: [],
        rowHeaders: ['rowNum'],
        bodyHeight: 650,
        treeColumnOptions: {
            name: 'menuNm',
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

    tui.Grid.applyTheme('clean');

    const form = {
        editMode: document.getElementById('editMode'),
        menuCd: document.getElementById('menuCd'),
        upMenuCd: document.getElementById('upMenuCd'),
        menuNm: document.getElementById('menuNm'),
        menuUrl: document.getElementById('menuUrl'),
        sortOrd: document.getElementById('sortOrd'),
        useYn: document.getElementById('dtlUseYn')
    };
    const authCheckboxes = document.querySelectorAll('input[name="authRoles"]');
    const actionCheckboxes = document.querySelectorAll('input[name="authAction"]');

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

    function syncActionRows() {
        authCheckboxes.forEach(cb => setActionRowEnabled(cb.value, cb.checked));
    }

    function resetForm(parentCd = '') {
        form.editMode.value = '1';
        form.menuCd.value = '';
        form.menuCd.readOnly = false;
        form.upMenuCd.value = parentCd;
        form.menuNm.value = '';
        form.menuUrl.value = '';
        form.sortOrd.value = '1';
        form.useYn.value = 'Y';
        authCheckboxes.forEach(cb => cb.checked = false);
        setDefaultActionPermissions();
        syncActionRows();
    }

    function buildAuthPermissions() {
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

        return { checkedRoles, authPermissions };
    }

    async function loadTreeData() {
        try {
            const res = await axios.get('/system/menu-manage/tree-data');
            grid.resetData(res.data || []);
            grid.expandAll();
        } catch (error) {
            console.error('Tree fetch error:', error);
        }
        setTimeout(() => grid.refreshLayout(), 100);
    }

    authCheckboxes.forEach(cb => {
        cb.addEventListener('change', () => setActionRowEnabled(cb.value, cb.checked));
    });
    resetForm('');
    await loadTreeData();

    grid.on('click', async (ev) => {
        if (ev.rowKey === undefined) return;
        const row = grid.getRow(ev.rowKey);
        if (!row) return;

        form.editMode.value = '2';
        form.menuCd.value = row.menuCd || '';
        form.menuCd.readOnly = true;
        form.upMenuCd.value = row.upMenuCd || '';
        form.menuNm.value = row.menuNm || '';
        form.menuUrl.value = row.menuUrl || '';
        form.sortOrd.value = row.sortOrd || '1';
        form.useYn.value = row.useYn || 'Y';

        authCheckboxes.forEach(cb => cb.checked = false);
        setDefaultActionPermissions();
        syncActionRows();

        try {
            const res = await axios.get(`/system/menu-manage/auth-data?menuCd=${row.menuCd}`);
            const permissions = res.data || [];
            const roles = permissions.map(auth => auth.authCd);

            authCheckboxes.forEach(cb => {
                cb.checked = roles.includes(cb.value);
                setActionRowEnabled(cb.value, cb.checked);
            });

            permissions.forEach(auth => {
                actionCheckboxes.forEach(cb => {
                    if (cb.dataset.role === auth.authCd) {
                        cb.checked = auth[cb.dataset.action] === 'Y';
                    }
                });
            });
        } catch (e) {
            console.error(e);
        }
    });

    function addTreeRowLogic(isSibling) {
        const focused = grid.getFocusedCell();
        let targetParentRowKey = null;
        let parentCd = '';
        let offset = undefined;
        const allData = grid.getData();

        if (focused && focused.rowKey !== null && focused.rowKey !== undefined) {
            const row = grid.getRow(focused.rowKey);

            if (isSibling) {
                const parentRow = allData.find(r => r.menuCd === row.upMenuCd);
                targetParentRowKey = parentRow ? parentRow.rowKey : null;
                parentCd = row.upMenuCd || '';

                const siblings = allData.filter(r => (r.upMenuCd || '') === parentCd);
                const index = siblings.findIndex(r => r.rowKey === row.rowKey);
                if (index !== -1) offset = index + 1;
            } else {
                targetParentRowKey = row.rowKey;
                parentCd = row.menuCd;
            }
        } else if (!isSibling) {
            CommonUtils.toast('상위 메뉴를 먼저 선택한 뒤 하위 메뉴를 추가하세요.', 'warning');
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
        } catch (e) {
            console.error('Tree append error: ', e);
            CommonUtils.toast('트리 렌더링 중 오류가 발생했습니다.', 'error');
        }

        resetForm(parentCd);
        form.menuNm.value = '[새 메뉴]';
        form.menuCd.focus();
    }

    document.getElementById('btn-add-child').addEventListener('click', () => addTreeRowLogic(false));
    document.getElementById('btn-add-sibling').addEventListener('click', () => addTreeRowLogic(true));

    document.getElementById('btn-delete-node').addEventListener('click', () => {
        const focused = grid.getFocusedCell();
        if (focused === null || focused.rowKey === null || focused.rowKey === undefined) {
            CommonUtils.toast('삭제할 메뉴를 선택하세요.', 'warning');
            return;
        }

        const row = grid.getRow(focused.rowKey);
        CommonUtils.confirm(`[${row.menuNm}] 메뉴를 삭제하시겠습니까?`, async () => {
            try {
                const params = new URLSearchParams();
                params.append('menuCd', row.menuCd);
                await axios.post('/system/menu-manage/delete', params);
                CommonUtils.toast('삭제되었습니다.', 'success');
                await loadTreeData();
                resetForm('');
            } catch (e) {
                console.error(e);
            }
        }, '메뉴 삭제 확인');
    });

    document.getElementById('btn-save').addEventListener('click', async () => {
        if (!form.menuCd.value.trim() || !form.menuNm.value.trim()) {
            CommonUtils.toast('메뉴 코드와 메뉴명은 필수입니다.', 'warning');
            form.menuCd.focus();
            return;
        }

        const { checkedRoles, authPermissions } = buildAuthPermissions();
        const reqData = {
            mode: form.editMode.value,
            menu: {
                menuCd: form.menuCd.value,
                upMenuCd: form.upMenuCd.value,
                menuNm: form.menuNm.value,
                menuUrl: form.menuUrl.value,
                sortOrd: parseInt(form.sortOrd.value || '1', 10),
                useYn: form.useYn.value
            },
            authRoles: checkedRoles,
            authPermissions: authPermissions
        };

        try {
            await axios.post('/system/menu-manage/save', reqData);
            CommonUtils.toast('저장되었습니다.', 'success');
            await loadTreeData();
            form.editMode.value = '2';
            form.menuCd.readOnly = true;
        } catch (e) {
            console.error(e);
        }
    });
});
