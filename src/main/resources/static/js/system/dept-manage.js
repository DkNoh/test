/**
 * dept-manage.js
 * 부서 관리 화면 (TuiPageBuilder 연동)
 */
document.addEventListener('DOMContentLoaded', function () {

    const pageBuilder = new TuiPageBuilder({
        el: 'grid',
        apiUrl: '/api/dept/search',
        searchInputs: ['depId', 'depNm'],
        rowHeaders: ['rowNum'],
        columns: [
            { header: '부서코드', name: 'depId', width: 150, align: 'center' },
            { header: '부서명', name: 'depNm', minWidth: 200, align: 'left' },
            { header: '사용여부', name: 'actYn', width: 120, align: 'center',
              formatter: ({value}) => value === 'Y' ? '<span class="badge badge-success">사용</span>' : '<span class="badge badge-fail">미사용</span>' },
            { header: '등록일시', name: 'wrtDttm', width: 200, align: 'center' }
        ],
        autoModal: true,
        autoModalTitle: '부서 상세 조회'
    });

    document.querySelector('#btn-create').addEventListener('click', () => {
        document.querySelector('#c-depId').value = '';
        document.querySelector('#c-depNm').value = '';
        document.querySelector('input[name="c-actYn"][value="Y"]').checked = true;
        document.getElementById('createModal').style.display = 'flex';
    });

    document.querySelector('#btn-save-dept').addEventListener('click', () => {
        const depId = document.querySelector('#c-depId').value.trim();
        const depNm = document.querySelector('#c-depNm').value.trim();
        const actYn = document.querySelector('input[name="c-actYn"]:checked').value;
        
        if (!depId) return CommonUtils.toast('부서코드를 입력하세요.', 'warning');
        if (!depNm) return CommonUtils.toast('부서명을 입력하세요.', 'warning');

        const data = { depId, depNm, actYn };

        CommonUtils.confirm(`[${depNm}] 부서를 등록하시겠습니까?`, async () => {
            try {
                await axios.post('/api/dept/create', data);
                CommonUtils.toast('부서가 성공적으로 등록되었습니다.', 'success');
                document.getElementById('createModal').style.display = 'none';
                pageBuilder.searchData(1);
            } catch (error) {
                // Interceptor 처리
            }
        });
    });
});
