document.addEventListener('DOMContentLoaded', function () {

    const fmt = n => (n ?? 0).toLocaleString();
    const pct = (n, t) => t ? (n / t * 100).toFixed(1) + '%' : '0.0%';

    // 오늘 날짜 기본값
    const today = new Date().toISOString().split('T')[0];
    document.querySelector('#searchDate').value = today;

    document.querySelector('#btn-search').addEventListener('click', load);
    document.querySelector('#btn-reset').addEventListener('click', () => {
        document.querySelector('#searchDate').value = today;
        resetUI();
    });

    load(); // 페이지 진입 시 오늘 자동 조회

    // ── 데이터 조회 ───────────────────────────────────────
    function load() {
        const date = document.querySelector('#searchDate').value;
        if (!date) { CommonUtils.toast('조회일자를 선택하세요.', 'warning'); return; }

        fetch('/sms/dept-stat/chart?searchDate=' + date)
            .then(r => r.json())
            .then(res => {
                const list = res.data || [];
                renderMetrics(list);
                renderChart(list);
                renderTable(list, date);
            })
            .catch(() => CommonUtils.toast('조회 중 오류가 발생했습니다.', 'error'));
    }

    // ── 메트릭 카드 ───────────────────────────────────────
    function renderMetrics(list) {
        const tot  = list.reduce((s, r) => s + (r.totalCount   || 0), 0);
        const succ = list.reduce((s, r) => s + (r.successCount || 0), 0);
        const fail = list.reduce((s, r) => s + (r.failCount    || 0), 0);
        const send = list.reduce((s, r) => s + (r.sendingCount || 0), 0);

        document.querySelector('#m-total').textContent   = fmt(tot);
        document.querySelector('#m-success').textContent = fmt(succ);
        document.querySelector('#m-fail').textContent    = fmt(fail);
        document.querySelector('#m-sending').textContent = fmt(send);
        document.querySelector('#m-srate').textContent   = pct(succ, tot);
        document.querySelector('#m-frate').textContent   = pct(fail, tot);
        document.querySelector('#m-wrate').textContent   = pct(send, tot);
    }

    // ── CoreUI Progress 기반 스택 바 차트 ─────────────────
    function renderChart(list) {
        const el = document.querySelector('#chart-bars');
        if (!list.length) {
            el.innerHTML = '<div class="text-center text-muted py-4">해당 일자의 발송 내역이 없습니다.</div>';
            return;
        }

        const maxTotal = Math.max(...list.map(r => r.totalCount || 0), 1);

        el.innerHTML = list.map(row => {
            const total   = row.totalCount   || 0;
            const success = row.successCount || 0;
            const fail    = row.failCount    || 0;
            const sending = row.sendingCount || 0;

            const trackW = (total / maxTotal * 100).toFixed(1);
            const sw = total ? (success / total * 100).toFixed(1) : 0;
            const fw = total ? (fail    / total * 100).toFixed(1) : 0;
            const ww = total ? (sending / total * 100).toFixed(1) : 0;

            const tip = `${row.timeSlot}: 성공 ${fmt(success)} / 실패 ${fmt(fail)} / 전송중 ${fmt(sending)}`;

            return `
            <div class="chart-row">
                <div class="chart-row-label">${row.timeSlot}</div>
                <div class="chart-row-track" style="max-width:${trackW}%" title="${tip}">
                    <div class="chart-seg bg-success" style="width:0" data-w="${sw}"></div>
                    <div class="chart-seg bg-danger"  style="width:0" data-w="${fw}"></div>
                    <div class="chart-seg bg-warning" style="width:0" data-w="${ww}"></div>
                </div>
                <div class="chart-row-count text-muted">${fmt(total)}</div>
            </div>`;
        }).join('');

        // 다음 프레임에 너비 적용 → CSS transition 발동
        requestAnimationFrame(() => {
            el.querySelectorAll('.chart-seg').forEach(seg => {
                seg.style.width = seg.dataset.w + '%';
            });
        });
    }

    // ── 상세 테이블 ───────────────────────────────────────
    function renderTable(list, date) {
        document.querySelector('#detail-date').textContent = date + ' 기준';
        const el = document.querySelector('#detail-table');

        if (!list.length) {
            el.innerHTML = '<div class="text-center text-muted py-4">데이터가 없습니다.</div>';
            return;
        }

        const rows = list.map(r => {
            const tot  = r.totalCount   || 0;
            const succ = r.successCount || 0;
            const fail = r.failCount    || 0;
            const send = r.sendingCount || 0;
            const rate = tot ? (succ / tot * 100).toFixed(1) : '0.0';
            const rn   = parseFloat(rate);
            const cls  = rn >= 90 ? 'bg-success' : rn >= 70 ? 'bg-warning text-dark' : 'bg-danger';

            return `<tr>
                <td class="text-center fw-semibold">${r.timeSlot}</td>
                <td class="text-center text-success fw-semibold">${fmt(succ)}</td>
                <td class="text-center text-danger">${fmt(fail)}</td>
                <td class="text-center text-warning fw-semibold">${fmt(send)}</td>
                <td class="text-center fw-bold">${fmt(tot)}</td>
                <td class="text-center">
                    <span class="badge rounded-pill ${cls}">${rate}%</span>
                </td>
            </tr>`;
        }).join('');

        el.innerHTML = `
        <table class="table table-hover table-sm align-middle mb-0">
            <thead class="table-light">
                <tr>
                    <th class="text-center" style="width:80px;">시간대</th>
                    <th class="text-center text-success">성공</th>
                    <th class="text-center text-danger">실패</th>
                    <th class="text-center text-warning">전송중</th>
                    <th class="text-center">총건수</th>
                    <th class="text-center" style="width:100px;">성공률</th>
                </tr>
            </thead>
            <tbody>${rows}</tbody>
        </table>`;
    }

    // ── 초기화 ────────────────────────────────────────────
    function resetUI() {
        ['#m-total','#m-success','#m-fail','#m-sending',
         '#m-srate','#m-frate','#m-wrate']
            .forEach(id => { document.querySelector(id).textContent = '—'; });
        document.querySelector('#chart-bars').innerHTML =
            '<div class="text-center text-muted py-5"><p style="font-size:2rem">📊</p>' +
            '<p class="mb-0">조회일자를 선택하고 조회 버튼을 클릭하세요.</p></div>';
        document.querySelector('#detail-table').innerHTML =
            '<div class="text-center text-muted py-4">조회 결과가 여기에 표시됩니다.</div>';
        document.querySelector('#detail-date').textContent = '';
    }
});
