document.addEventListener('DOMContentLoaded', function () {

    const pageBuilder = new TuiPageBuilder({
        el: 'grid',
        apiUrl: '/api/user/search',
        searchInputs: ['empId', 'empName', 'deptId'],
        rowHeaders: ['rowNum'],
        columns: [
            { header: '사번(ID)',   name: 'empId',       width: 120, align: 'center' },
            { header: '사용자명',   name: 'empName',     minWidth: 120, align: 'center' },
            { header: '부서코드',   name: 'deptId',      width: 100, align: 'center' },
            { header: '부서명',     name: 'deptNm',      width: 120, align: 'center' },
            { header: '역할',       name: 'userRole',    width: 120, align: 'center',
              formatter: ({value}) => value === 'ROLE_ADMIN' ? '시스템관리자' : '일반사용자' },
            { header: '조회권한',   name: 'authRead',    width: 80,  align: 'center' },
            { header: '승인권한',   name: 'authApprove', width: 80,  align: 'center' },
            { header: '캠페인권한', name: 'authCampaign',width: 90,  align: 'center' },
            { header: '사용여부',   name: 'useYn',       width: 90,  align: 'center',
              formatter: ({value}) => value === 'Y' ? '<span class="badge badge-success">사용</span>' : '<span class="badge badge-fail">미사용</span>' },
            { header: '최근접속',   name: 'lastLoginTime', width: 160, align: 'center' }
        ]
    });

    document.querySelector('#btn-create').addEventListener('click', () => {
        document.querySelector('#c-empId').value = '';
        document.querySelector('#c-empName').value = '';
        document.querySelector('#c-deptId').value = '';
        document.querySelector('#c-userRole').value = 'ROLE_USER';
        document.querySelector('#c-authRead').checked = true;
        document.querySelector('#c-authApprove').checked = false;
        document.querySelector('#c-authCampaign').checked = false;
        document.querySelector('input[name="c-useYn"][value="Y"]').checked = true;
        document.querySelector('#createModal').style.display = 'flex';
    });

    document.querySelector('#btn-save-user').addEventListener('click', () => {
        const empId    = document.querySelector('#c-empId').value.trim();
        const empName  = document.querySelector('#c-empName').value.trim();
        const deptId   = document.querySelector('#c-deptId').value.trim();

        if (!empId)   return CommonUtils.toast('사용자ID를 입력하세요.', 'warning');
        if (!empName) return CommonUtils.toast('사용자명을 입력하세요.', 'warning');

        const userRole    = document.querySelector('#c-userRole').value;
        const authRead    = document.querySelector('#c-authRead').checked    ? 'Y' : 'N';
        const authApprove = document.querySelector('#c-authApprove').checked ? 'Y' : 'N';
        const authCampaign= document.querySelector('#c-authCampaign').checked? 'Y' : 'N';
        const useYn       = document.querySelector('input[name="c-useYn"]:checked').value;

        // TB_EMP 컬럼에 맞는 JSON Payload
        const data = { empId, empName, deptId, userRole, authRead, authApprove, authCampaign, useYn };

        CommonUtils.confirm(`[${empName}] 계정을 생성하시겠습니까?`, async () => {
            try {
                await axios.post('/api/user/create', data);
                CommonUtils.toast('계정이 성공적으로 생성되었습니다.', 'success');
                document.querySelector('#createModal').style.display = 'none';
                pageBuilder.searchData(1);
            } catch (error) {
                // Interceptor에서 에러 토스트를 띄워줌
            }
        });
    });
});
