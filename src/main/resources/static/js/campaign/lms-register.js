document.addEventListener('DOMContentLoaded', function () {

    const msgContent = document.querySelector('#msgContent');
    msgContent.addEventListener('input', () => {
        document.querySelector('#charCount').textContent = msgContent.value.length;
    });

    document.querySelector('#btn-submit').addEventListener('click', async () => {
        const campaignName = document.querySelector('#campaignName').value.trim();
        const senderNo     = document.querySelector('#senderNo').value.trim();
        const receiverNos  = document.querySelector('#receiverNos').value.trim();
        const message      = msgContent.value.trim();

        if (!campaignName) return CommonUtils.toast('캠페인명을 입력하세요.', 'warning');
        if (!senderNo)     return CommonUtils.toast('발신번호를 입력하세요.', 'warning');
        if (!receiverNos)  return CommonUtils.toast('수신번호를 입력하세요.', 'warning');
        if (!message)      return CommonUtils.toast('메세지 내용을 입력하세요.', 'warning');

        const lines = receiverNos.split('\n').filter(l => l.trim());
        const data = {
            campaignName,
            senderNo,
            receiverNos: lines.join(','),
            targetCount: lines.length,
            msgContent: message,
            reservedAt: document.querySelector('#reservedAt').value || null
        };

        CommonUtils.confirm(`[${campaignName}] LMS 발송을 승인 요청하시겠습니까?`, async () => {
            try {
                await axios.post('/campaign/lms/submit', data);
                CommonUtils.toast('승인 요청이 완료되었습니다.', 'success');
                setTimeout(() => location.href = '/campaign/target-manage', 1500);
            } catch (e) {}
        });
    });
});
