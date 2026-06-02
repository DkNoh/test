/**
 * dashboard.js
 * 대시보드 데이터를 10초마다 AJAX로 폴링하여 갱신
 */
document.addEventListener('DOMContentLoaded', function () {
    const POLL_INTERVAL = 10000; // 10초

    function fetchDashboardData() {
        axios.get('/api/dashboard/summary')
            .then(res => {
                const data = res.data;
                renderKpi(data.kpi);
                renderWeeklyTrend(data.weeklyTrend);
                renderChannelRatio(data.channelRatio);
                renderFailLogs(data.recentFailLogs);
            })
            .catch(err => {
                console.error('대시보드 데이터 조회 실패:', err);
            });
    }

    function renderKpi(kpi) {
        if (!kpi) return;
        document.getElementById('kpi-total').textContent = kpi.totalSendCount.toLocaleString();
        document.getElementById('kpi-success').textContent = kpi.successRate.toFixed(1);
        document.getElementById('kpi-fail').textContent = kpi.failCount.toLocaleString();
        document.getElementById('kpi-cost').textContent = kpi.estimatedCost.toLocaleString();
    }

    function renderWeeklyTrend(trends) {
        const container = document.getElementById('weekly-trend-container');
        if (!trends || !container) return;
        
        container.innerHTML = '';
        trends.forEach(trend => {
            container.innerHTML += `
                <div class="mb-3">
                    <div class="d-flex justify-content-between mb-1">
                        <span class="fw-medium text-muted">${trend.dayOfWeek}</span>
                        <span class="fw-bold">${trend.sendCount.toLocaleString()} 건</span>
                    </div>
                    <div class="progress" style="height: 12px;">
                        <div class="progress-bar bg-primary" role="progressbar" style="width: ${trend.percentage}%"></div>
                    </div>
                </div>
            `;
        });
    }

    function renderChannelRatio(ratios) {
        const container = document.getElementById('channel-ratio-container');
        if (!ratios || !container) return;
        
        container.innerHTML = '';
        ratios.forEach(ratio => {
            container.innerHTML += `
                <div class="mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        <div class="d-flex align-items-center gap-2">
                            <span class="badge ${ratio.colorClass} rounded-pill" style="width: 10px; height: 10px; padding: 0;">&nbsp;</span>
                            <span class="fw-bold">${ratio.channelName}</span>
                        </div>
                        <span class="fw-bold fs-5">${ratio.ratio.toFixed(1)}%</span>
                    </div>
                    <div class="progress" style="height: 8px;">
                        <div class="progress-bar ${ratio.colorClass}" style="width: ${ratio.ratio}%"></div>
                    </div>
                </div>
            `;
        });
    }

    function renderFailLogs(logs) {
        const tbody = document.getElementById('fail-log-tbody');
        if (!logs || !tbody) return;
        
        tbody.innerHTML = '';
        logs.forEach(log => {
            let badgeClass = 'bg-secondary';
            if (log.sendType === 'SMS') badgeClass = 'bg-primary text-white';
            else if (log.sendType === 'LMS') badgeClass = 'bg-success text-white';
            else if (log.sendType === 'MMS') badgeClass = 'bg-danger text-white';
            else if (log.sendType === 'ALIMTALK') badgeClass = 'bg-warning text-dark';

            tbody.innerHTML += `
                <tr>
                    <td>${log.failDate}</td>
                    <td><span class="badge ${badgeClass}">${log.sendType}</span></td>
                    <td>${log.receiverNo}</td>
                    <td class="text-danger fw-bold">${log.errorCode}</td>
                    <td class="text-start">${log.failReason}</td>
                    <td><button class="btn btn-sm btn-light border">재시도</button></td>
                </tr>
            `;
        });
    }

    // 1. 페이지 로딩 즉시 1회 호출
    fetchDashboardData();

    // 2. 이후 10초 주기로 폴링
    setInterval(fetchDashboardData, POLL_INTERVAL);
});
