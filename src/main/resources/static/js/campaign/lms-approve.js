document.addEventListener('DOMContentLoaded', () => loadPending('lms'));

function loadPending(type) {
    fetch(`/campaign/${type}/pending`)
        .then(r => r.json())
        .then(res => {
            const list = res.data || [];
            document.querySelector('#total-count').textContent = list.length;
            const tbody = document.querySelector('#pendingBody');
            if (!list.length) { tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">승인 대기 건이 없습니다.</td></tr>'; return; }
            tbody.innerHTML = list.map((c, i) => `
                <tr>
                    <td class="text-center">${i+1}</td>
                    <td>${c.campaignName||''}</td>
                    <td class="text-center">${c.targetCount??0}</td>
                    <td class="text-center">${c.regId||''}</td>
                    <td class="text-center">${c.regDt?c.regDt.substring(0,16).replace('T',' '):''}</td>
                    <td class="text-center d-flex gap-1 justify-content-center">
                        <button class="btn btn-sm btn-success" onclick="process(${c.campaignId},'APPROVED')">승인</button>
                        <button class="btn btn-sm btn-danger"  onclick="process(${c.campaignId},'REJECTED')">반려</button>
                    </td>
                </tr>`).join('');
        });
}

async function process(campaignId, action) {
    CommonUtils.confirm(`해당 캠페인을 ${action==='APPROVED'?'승인':'반려'}하시겠습니까?`, async () => {
        try { await axios.post('/campaign/approve', {campaignId, action}); CommonUtils.toast('처리되었습니다.', 'success'); loadPending('lms'); } catch(e) {}
    });
}
