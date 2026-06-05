document.addEventListener('DOMContentLoaded', function () {

    // 조회기간 기본값: 당일 00:00 ~ 23:59
    CommonUtils.setDefaultDateTime();


    const statusBadge = v =>
        v === 'SUCCESS' ? '<span class="badge badge-success">성공</span>'
        : v === 'FAIL'  ? '<span class="badge badge-fail">실패</span>'
        : v === 'WAIT'  ? '<span class="badge" style="background:#ffc107;color:#000;">대기</span>'
        : (v ?? '');

    axios.get('/api/common-code/dept')
        .then(res => {
            const deptSel = document.querySelector('#bizDept');
            (res.data || []).forEach(d => {
                const opt = document.createElement('option');
                opt.value = d.code; opt.textContent = d.name;
                deptSel.appendChild(opt);
            });
        });

    document.querySelector('#bizDept').addEventListener('change', function () {
        const msgSel = document.querySelector('#sendType');
        msgSel.innerHTML = '<option value="">전체</option>';
        if (!this.value) { msgSel.disabled = true; return; }
        axios.get('/api/common-code/messages', { params: { deptId: this.value } })
            .then(res => {
                (res.data || []).forEach(m => {
                    const opt = document.createElement('option');
                    opt.value = m.code; opt.textContent = m.name;
                    msgSel.appendChild(opt);
                });
                msgSel.disabled = false;
            });
    });

    new TuiPageBuilder({
        el: 'grid',
        apiUrl: '/sms/history/data',
        searchInputs: ['startDateTime', 'endDateTime', 'sendStatus', 'receiverNo', 'sendType'],
        rowHeaders: ['rowNum'],
        columns: [
            { header: '발송시간',   name: 'sentAt',     width: 160, align: 'center' },
            { header: '받는폰번호', name: 'receiverNo', width: 140, align: 'center' },
            { header: '내용',       name: 'message',    minWidth: 200, align: 'left' },
            { header: '회신번호',   name: 'senderNo',   width: 130, align: 'center' },
            { header: '상태',       name: 'sendStatus', width: 90,  align: 'center',
              formatter: ({value}) => statusBadge(value) },
            { header: '통신사',     name: 'telecom',    width: 90,  align: 'center' },
            { header: '취소',       name: 'resendYn',   width: 80,  align: 'center',
              formatter: ({row}) => row.sendStatus === 'WAIT'
                ? `<button class="btn btn-sm btn-danger btn-cancel" data-id="${row.id}" style="padding:1px 8px;font-size:11px;">취소</button>` : '' }
        ]
    });

    document.querySelector('#btn-reset')?.addEventListener('click', () => {
        document.querySelector('#sendStatus').value   = '';
        document.querySelector('#receiverNo').value   = '';
        document.querySelector('#bizDept').value      = '';
        document.querySelector('#sendType').innerHTML = '<option value="">부서 선택 후</option>';
        document.querySelector('#sendType').disabled  = true;
        CommonUtils.setDefaultDateTime(true);
    });

    document.querySelector('#grid').addEventListener('click', e => {
        const btn = e.target.closest('.btn-cancel');
        if (!btn) return;
        CommonUtils.confirm('해당 발송 건을 취소하시겠습니까?', async () => {
            try {
                await axios.post('/sms/history/delete', null, { params: { id: btn.dataset.id } });
                CommonUtils.toast('취소 처리되었습니다.', 'success');
                document.querySelector('#btn-search').click();
            } catch (e) {}
        });
    });
});
