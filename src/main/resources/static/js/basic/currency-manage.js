document.addEventListener('DOMContentLoaded', function () {

    // ── 기준일자 당일 기본값 ──────────────────────────────────
    const today = new Date().toISOString().split('T')[0];
    const baseDtEl = document.querySelector('#baseDt');
    if (baseDtEl && !baseDtEl.value) baseDtEl.value = today;

    // ── 은행코드 콤보 초기 로드 ──────────────────────────────
    fetch('/api/common-code/bank')
        .then(r => r.json())
        .then(res => {
            const combo = document.querySelector('#bankCdCombo');
            (res.data || []).forEach(b => {
                const opt = document.createElement('option');
                opt.value = b.code;
                opt.textContent = `${b.code} — ${b.name}`;
                combo.appendChild(opt);
            });
        });

    // ── 은행코드 자동완성 (CommonUtils 공통 기능 사용) ────────
    CommonUtils.initAutocomplete({
        inputEl:   '#bankCdText',
        balloonEl: '#bankBalloon',
        apiUrl:    '/api/common-code/bank',
        syncCombo: '#bankCdCombo',
        minLength:  1,
        debounceMs: 200
    });

    // ── bankCd hidden input 동기화 ────────────────────────────
    const bankCdHidden = Object.assign(document.createElement('input'),
        { type: 'hidden', id: 'bankCd' });
    document.body.appendChild(bankCdHidden);

    const syncBankCd = () => {
        const txt   = document.querySelector('#bankCdText').value;
        const combo = document.querySelector('#bankCdCombo').value;
        bankCdHidden.value = txt || combo;
    };
    document.querySelector('#bankCdText').addEventListener('input',  syncBankCd);
    document.querySelector('#bankCdCombo').addEventListener('change', syncBankCd);

    // ── TuiPageBuilder ────────────────────────────────────────
    const pageBuilder = new TuiPageBuilder({
        el: 'grid',
        apiUrl: '/basic/currency/data',
        searchInputs: ['baseDt', 'currencyCd', 'bankCd'],
        rowHeaders: ['rowNum'],
        columns: [
            { header: '기준일자',   name: 'baseDt',       width: 110, align: 'center' },
            { header: '통화코드',   name: 'currencyCd',   width: 90,  align: 'center' },
            { header: '통화명',     name: 'currencyNm',   width: 120, align: 'center' },
            { header: '국가명',     name: 'countryNm',    width: 120, align: 'center' },
            { header: '단위',       name: 'unit',         width: 70,  align: 'center' },
            { header: '제공기관',   name: 'providerNm',   width: 120, align: 'center' },
            { header: '기준환율',   name: 'baseRate',     width: 110, align: 'right' },
            { header: '현금매수',   name: 'cashBuyRate',  width: 110, align: 'right' },
            { header: '현금매도',   name: 'cashSellRate', width: 110, align: 'right' },
            { header: '송금보낼때', name: 'sendRate',     width: 110, align: 'right' },
            { header: '송금받을때', name: 'recvRate',     width: 110, align: 'right' }
        ]
    });

    // ── 엑셀 다운로드 ─────────────────────────────────────────
    document.querySelector('#btn-excel')?.addEventListener('click', () => {
        const qs = `?baseDt=${document.querySelector('#baseDt').value}`
                 + `&currencyCd=${document.querySelector('#currencyCd').value}`
                 + `&bankCd=${bankCdHidden.value}`;
        window.location.href = pageBuilder.config.apiUrl.replace('/data', '') + '/excel' + qs;
    });

    document.querySelector('#btn-excel-grid')?.addEventListener('click', () => {
        pageBuilder.getGrid().export('xlsx', { fileName: '환율조회_화면데이터' });
    });
});
