document.addEventListener('DOMContentLoaded', function () {

    const statusBadge = v =>
        v === 'SUCCESS' ? '<span class="badge badge-success">성공</span>'
        : v === 'FAIL'  ? '<span class="badge badge-fail">실패</span>'
        : v === 'WAIT'  ? '<span class="badge" style="background:#ffc107;color:#000;">대기</span>'
        : (v ?? '');

    new TuiPageBuilder({
        el: 'grid',
        apiUrl: '/sms/customer/data',
        searchInputs: ['msgType', 'receiverNo', 'searchDate'],
        rowHeaders: ['rowNum'],
        columns: [
            { header: '발송시간',   name: 'sentAt',       width: 160, align: 'center' },
            { header: '받은시간',   name: 'receivedAt',   width: 160, align: 'center' },
            { header: '예금주명',   name: 'holderName',   width: 120, align: 'center' },
            { header: '수신번호',   name: 'receiverNo',   width: 140, align: 'center' },
            { header: '회신번호',   name: 'senderNo',     width: 130, align: 'center' },
            { header: '메세지내용', name: 'message',      minWidth: 200, align: 'left' },
            { header: '중개업체',   name: 'intermediary', width: 100, align: 'center' },
            { header: '결과',       name: 'sendStatus',   width: 90,  align: 'center',
              formatter: ({value}) => statusBadge(value) },
            { header: '부서',       name: 'deptNm',       width: 120, align: 'center' }
        ]
    });

    const hiddenMsgType = Object.assign(document.createElement('input'), { type: 'hidden', id: 'msgType', value: 'SMS' });
    document.body.appendChild(hiddenMsgType);
    document.querySelectorAll('input[name="msgType"]').forEach(r =>
        r.addEventListener('change', () => { hiddenMsgType.value = r.value; })
    );
});
