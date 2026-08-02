localStorage.setItem('sms.user', JSON.stringify({ name: 'Ravi Mehta', email: 'ravi.mehta@sms.edu', role: 'STUDENT' }));
renderLayout('dashboard', 'Welcome back, Ravi', [{ label: 'Home' }, { label: 'My dashboard' }]);
setPageContent(`
  <div class="stat-grid mb-3">
    <div class="stat-card">
<div class="icon"><i class="fa-solid fa-percent"></i></div><div><div class="label">Attendance</div><div class="value">88.4%</div></div></div>
    <div class="stat-card accent">
<div class="icon"><i class="fa-solid fa-square-poll-vertical"></i>
</div><div><div class="label">CGPA</div><div class="value">8.42</div></div></div>
    <div class="stat-card warning"><div class="icon"><i class="fa-solid fa-money-bill"></i></div><div><div class="label">Fees due</div><div class="value">₹12,000</div></div></div>
    <div class="stat-card success"><div class="icon"><i class="fa-solid fa-book"></i></div><div><div class="label">Subjects</div><div class="value">6</div></div></div>
  </div>
  <div class="row">
    <div class="col card chart-card" style="flex:2"><div class="card-title"><h3>Subject-wise attendance</h3></div><canvas id="c"></canvas></div>
    <div class="col card"><div class="card-title"><h3>Today's classes</h3></div>
      ${window.DEMO_TIMETABLE[0].slots.map(s=>`<div class="flex-between" style="padding:10px 0;border-bottom:1px solid var(--border)">
        <div><div style="font-weight:500">${s.subject}</div><div class="text-muted" style="font-size:12px">${s.teacher} · ${s.room}</div></div>
        <span class="badge info">${s.time}</span></div>`).join('')}
    </div>
  </div>`);
new Chart(document.getElementById('c'),{type:'bar',
  data:{labels:['DS','Algo','OS','DB','Net','ML'],datasets:[{data:[92,86,78,90,84,72],backgroundColor:'#14B8A6',borderRadius:6}]},
  options:{plugins:{legend:{display:false}},scales:{y:{min:50,max:100}}}});