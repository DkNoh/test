document.addEventListener('DOMContentLoaded', function () {
    new TuiPageBuilder({
        el: 'grid',
        apiUrl: '/sms/ssn/data',
        searchInputs: ['receiverNo'],
        rowHeaders: ['rowNum'],
        columns: [
            { header: '휴대폰번호',   name: 'phoneNo',   width: 160, align: 'center' },
            { header: '[식별자 숨김]', name: 'ssnMasked', width: 160, align: 'center' }
        ]
    });
});
