
  renderLayout('dashboard', 'Admin Dashboard', [{label:'Home'}, {label:'Dashboard'}]);
  setPageActions(`
    <button class="btn"><i class="fa-solid fa-download"></i> Export</button>
    <button class="btn btn-primary"><i class="fa-solid fa-plus"></i> Quick Add</button>
  `);

  (async function () {
    const d = await SmsApi.dashboardSummary();
    const inr = n => '₹' + new Intl.NumberFormat('en-IN').format(n);

    setPageContent(`
      <div class="stat-grid">
        <div class="stat-card"><div class="icon"><i class="fa-solid fa-user-graduate"></i></div>
          <div><div class="label">Total Students</div><div class="value">${d.totalStudents.toLocaleString()}</div><div class="trend"><i class="fa-solid fa-arrow-up"></i> +4.2% this month</div></div></div>
        <div class="stat-card accent"><div class="icon"><i class="fa-solid fa-chalkboard-user"></i></div>
          <div><div class="label">Total Teachers</div><div class="value">${d.totalTeachers}</div><div class="trend"><i class="fa-solid fa-arrow-up"></i> +1.8%</div></div></div>
        <div class="stat-card success"><div class="icon"><i class="fa-solid fa-clipboard-check"></i></div>
          <div><div class="label">Today's Attendance</div><div class="value">${d.todayAttendance}%</div><div class="trend"><i class="fa-solid fa-arrow-up"></i> +0.6%</div></div></div>
        <div class="stat-card warning"><div class="icon"><i class="fa-solid fa-sack-dollar"></i></div>
          <div><div class="label">Revenue (Month)</div><div class="value">${inr(d.revenueMonth)}</div><div class="trend"><i class="fa-solid fa-arrow-up"></i> +12.4%</div></div></div>
        <div class="stat-card danger"><div class="icon"><i class="fa-solid fa-triangle-exclamation"></i></div>
          <div><div class="label">Pending Fees</div><div class="value">${inr(d.pendingFees)}</div><div class="trend down"><i class="fa-solid fa-arrow-down"></i> -3.1%</div></div></div>
        <div class="stat-card"><div class="icon"><i class="fa-solid fa-file-pen"></i></div>
          <div><div class="label">Upcoming Exams</div><div class="value">${d.upcomingExams}</div><div class="trend">Next: Jul 10</div></div></div>
      </div>

      <div class="row mt-3">
        <div class="col card chart-card" style="flex:2">
          <div class="card-title"><h3>Admissions — last 12 months</h3>
            <select class="select" style="width:auto;padding:6px 10px"><option>2026</option><option>2025</option></select></div>
          <canvas id="adChart"></canvas>
        </div>
        <div class="col card chart-card">
          <div class="card-title"><h3>Departments</h3></div>
          <canvas id="deptChart"></canvas>
        </div>
      </div>

      <div class="row mt-3">
        <div class="col card chart-card" style="flex:2">
          <div class="card-title"><h3>Daily attendance — last 30 days</h3></div>
          <canvas id="attChart"></canvas>
        </div>
        <div class="col card chart-card">
          <div class="card-title"><h3>Revenue — last 12 months</h3></div>
          <canvas id="revChart"></canvas>
        </div>
      </div>

      <div class="row mt-3">
        <div class="col card" style="flex:2">
          <div class="card-title"><h3>Recent admissions</h3><a href="students.html">View all</a></div>
          <div class="table-wrap"><table class="table"><thead><tr><th>Student</th><th>Course</th><th>Date</th><th></th></tr></thead>
            <tbody>${d.recentAdmissions.map(r => `
              <tr><td>${r.name}</td><td>${r.course}</td><td>${r.date}</td><td class="text-right"><button class="btn btn-ghost"><i class="fa-solid fa-eye"></i></button></td></tr>
            `).join('')}</tbody></table></div>
        </div>
        <div class="col card">
          <div class="card-title"><h3>Upcoming exams</h3><a href="exams.html">View all</a></div>
          ${window.DEMO_EXAMS.map(e => `
            <div class="flex-between" style="padding:10px 0;border-bottom:1px solid var(--border)">
              <div><div style="font-weight:500">${e.name}</div><div class="text-muted" style="font-size:12px">${e.course} · Sem ${e.semester}</div></div>
              <span class="badge info">${e.start}</span>
            </div>`).join('')}
        </div>
      </div>
    `);

    const dark = (localStorage.getItem('sms.theme') === 'dark');
    const tickColor = dark ? '#94A3B8' : '#64748B';
    const gridColor = dark ? 'rgba(255,255,255,.06)' : 'rgba(15,23,42,.06)';
    Chart.defaults.font.family = 'Poppins';
    Chart.defaults.color = tickColor;

    new Chart(document.getElementById('adChart'), {
      type: 'line',
      data: { labels: ['Jul','Aug','Sep','Oct','Nov','Dec','Jan','Feb','Mar','Apr','May','Jun'],
        datasets: [{ label: 'Admissions', data: d.admissions12m, borderColor: '#2563EB',
          backgroundColor: 'rgba(37,99,235,.12)', fill: true, tension: .35, borderWidth: 2, pointRadius: 3 }] },
      options: { plugins:{ legend:{ display:false } }, scales:{ x:{ grid:{ color: gridColor }}, y:{ grid:{ color: gridColor }} } }
    });
    new Chart(document.getElementById('deptChart'), {
      type: 'doughnut',
      data: { labels: ['CSE','ECE','ME','MBA','Other'],
        datasets: [{ data: [820,540,460,380,640], backgroundColor:['#2563EB','#14B8A6','#F59E0B','#EF4444','#94A3B8'], borderWidth:0 }] },
      options: { cutout: '65%', plugins:{ legend:{ position:'bottom' } } }
    });
    new Chart(document.getElementById('attChart'), {
      type: 'bar',
      data: { labels: d.attendance30d.map((_,i)=>i+1), datasets:[{ label:'%', data:d.attendance30d, backgroundColor:'#14B8A6', borderRadius:6 }] },
      options: { plugins:{ legend:{display:false}}, scales:{ y:{ min:70, max:100, grid:{ color:gridColor }}, x:{ grid:{display:false}}}}
    });
    new Chart(document.getElementById('revChart'), {
      type: 'line',
      data: { labels: ['Jul','Aug','Sep','Oct','Nov','Dec','Jan','Feb','Mar','Apr','May','Jun'],
        datasets:[{ label:'Revenue', data: d.revenue12m, borderColor:'#F59E0B', backgroundColor:'rgba(245,158,11,.12)', fill:true, tension:.35 }] },
      options: { plugins:{legend:{display:false}}, scales:{x:{grid:{color:gridColor}},y:{grid:{color:gridColor}}}}
    });
  })();
