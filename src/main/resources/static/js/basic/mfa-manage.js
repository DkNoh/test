document.addEventListener('DOMContentLoaded', function () {

    const roleLabel = (value) => {
        if (value === 'ROLE_ADMIN') return '관리자';
        if (value === 'ROLE_USER')  return '일반사용자';
        return value ?? '';
    };

    new TuiPageBuilder({
        el: 'grid',
        apiUrl: '/basic/mfa/data',
        searchInputs: ['userGroupCode', 'userRole', 'empName', 'empId'],
        rowHeaders: ['rowNum'],
        columns: [
            { header: '사용자그룹코드', name: 'userGroupCode', width: 140, align: 'center' },
            { header: '사용자그룹명',   name: 'userGroupNm',   width: 130, align: 'center',
              formatter: ({value}) => roleLabel(value) },
            { header: '사용자ID',      name: 'empId',          width: 120, align: 'center' },
            { header: '사용자명',      name: 'empName',        minWidth: 120, align: 'center' },
            { header: '레벨',          name: 'userRole',       width: 120, align: 'center',
              formatter: ({value}) => roleLabel(value) },
            { header: '사용여부',      name: 'useYn',          width: 90,  align: 'center',
              formatter: ({value}) => value === 'Y'
                ? '<span class="badge badge-success">사용</span>'
                : '<span class="badge badge-fail">미사용</span>' },
            { header: 'MFA사용여부',   name: 'mfaYn',          width: 110, align: 'center',
              formatter: ({value}) => value === 'Y'
                ? '<span class="badge badge-success">사용</span>'
                : '<span class="badge badge-fail">미사용</span>' }
        ]
    });
});
