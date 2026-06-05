document.addEventListener('DOMContentLoaded', function () {

    // 카카오 템플릿 로드
    fetch('/api/common-code/messages?deptId=')
        .then(r => r.json())
        .then(res => {
            const sel = document.querySelector('#templateCode');
            (res.data || []).filter(m => m.type === 'KAKAO').forEach(m => {
                const opt = document.createElement('option');
                opt.value = m.code; opt.textContent = m.name;
                sel.appendChild(opt);
            });
        });

    document.querySelector('#btn-submit').addEventListener('click', async () => {
        const campaignName = document.querySelector('#campaignName').value.trim();
        const receiverNos  = document.querySelector('#receiverNos').value.trim();
        const message      = document.querySelector('#msgContent').value.trim();

        if (!campaignName) return CommonUtils.toast('캠페인명을 입력하세요.', 'warning');
        if (!receiverNos)  return CommonUtils.toast('수신번호를 입력하세요.', 'warning');
        if (!message)      return CommonUtils.toast('메세지 내용을 입력하세요.', 'warning');

        const lines = receiverNos.split('\n').filter(l => l.trim());
        const data = {
            campaignName,
            receiverNos: lines.join(','),
            targetCount: lines.length,
            msgContent: message,
            reservedAt: document.querySelector('#reservedAt').value || null
        };

        CommonUtils.confirm(`[${campaignName}] 알림톡 발송을 승인 요청하시겠습니까?`, async () => {
            try {
                await axios.post('/campaign/alimtalk/submit', data);
                CommonUtils.toast('승인 요청이 완료되었습니다.', 'success');
                setTimeout(() => location.href = '/campaign/target-manage', 1500);
            } catch (e) {}
        });
    });
});
