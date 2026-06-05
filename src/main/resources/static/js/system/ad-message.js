document.addEventListener('DOMContentLoaded', function () {
    new TuiPageBuilder({
        el: 'grid',
        apiUrl: '/system/ad-message/data',
        searchInputs: ['deptId', 'msgCode', 'deptNm', 'msgTitle'],
        rowHeaders: ['rowNum'],
        columns: [
            { header: '부서명',     name: 'deptNm',   width: 130, align: 'center' },
            { header: '메세지제목', name: 'msgTitle',  minWidth: 200, align: 'left' },
            { header: '메세지코드', name: 'msgCode',   width: 130, align: 'center' },
            { header: '사용여부',   name: 'useYn',    width: 90,  align: 'center' },
            { header: '등록일',     name: 'regDt',    width: 160, align: 'center' }
        ]
    });
});
