document.addEventListener('DOMContentLoaded', function () {
    const hiddenMsgType = Object.assign(document.createElement('input'), { type: 'hidden', id: 'msgType', value: '' });
    document.body.appendChild(hiddenMsgType);
    document.querySelectorAll('input[name="msgType"]').forEach(r =>
        r.addEventListener('change', () => { hiddenMsgType.value = r.value; })
    );

    new TuiPageBuilder({
        el: 'grid',
        apiUrl: '/system/message-manage/data',
        searchInputs: ['msgType', 'deptId', 'msgCode', 'deptNm', 'msgTitle'],
        rowHeaders: ['rowNum'],
        columns: [
            { header: '부서명',     name: 'deptNm',   width: 130, align: 'center' },
            { header: '메세지제목', name: 'msgTitle',  minWidth: 200, align: 'left' },
            { header: '메세지코드', name: 'msgCode',   width: 130, align: 'center' },
            { header: '사용여부',   name: 'useYn',    width: 90,  align: 'center' },
            { header: '유형',       name: 'msgType',  width: 90,  align: 'center' },
            { header: '등록일',     name: 'regDt',    width: 160, align: 'center' }
        ]
    });
});
