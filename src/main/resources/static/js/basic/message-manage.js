document.addEventListener('DOMContentLoaded', function () {

    new TuiPageBuilder({
        el: 'grid',
        apiUrl: '/basic/message/data',
        searchInputs: [],
        rowHeaders: ['rowNum'],
        columns: [
            { header: '부서명',    name: 'deptNm',     minWidth: 150, align: 'left' },
            { header: '코드',      name: 'msgCode',    width: 120,   align: 'center' },
            { header: '금지대상',  name: 'forbidYn',   width: 100,   align: 'center',
              formatter: ({value}) => value === 'Y' ? '<span class="badge badge-fail">금지</span>' : '' },
            { header: '메세지제목', name: 'msgTitle',  minWidth: 200, align: 'left' },
            { header: '등록일자',  name: 'regDt',      width: 120,   align: 'center' },
            { header: '발송시점',  name: 'sendPoint',  width: 120,   align: 'center' },
            { header: '메세지내용', name: 'msgContent', minWidth: 300, align: 'left' }
        ]
    });
});
