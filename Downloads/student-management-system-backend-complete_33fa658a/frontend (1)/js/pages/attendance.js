
renderLayout('attendance','Attendance',[{label:'Operations'},{label:'Attendance'}]);
setPageActions(`<button class="btn"><i class="fa-solid fa-file-pdf"></i> Monthly Report</button>
  <button class="btn btn-primary"><i class="fa-solid fa-pen-to-square"></i> Mark Attendance</button>`);
setPageContent(`
  <div class="stat-grid mb-2">
    <div class="stat-card success"><div class="icon"><i class="fa-solid fa-user-check"></i></div><div><div class="label">Present today</div><div class="value">2624</div></div></div>
    <div class="stat-card danger"><div class="icon"><i class="fa-solid fa-user-xmark"></i></div><div><div class="label">Absent today</div><div class="value">186</div></div></div>
    <div class="stat-card warning"><div class="icon"><i class="fa-solid fa-clock"></i></div><div><div class="label">Late entries</div><div class="value">30</div></div></div>
    <div class="stat-card"><div class="icon"><i class="fa-solid fa-percent"></i></div><div><div class="label">Attendance %</div><div class="value">92.4%</div></div></div>
  </div>

  <div class="row">
    <div class="col card chart-card" style="flex:2"><div class="card-title"><h3>Last 30 days</h3></div><canvas id="attChart"></canvas></div>
    <div class="col card"><div class="card-title"><h3>Leave requests</h3></div>
      ${[1,2,3].map((_,i)=>`<div class="flex-between" style="padding:10px 0;border-bottom:1px solid var(--border)">
        <div><div style="font-weight:500">${['Ravi M.','Sneha P.','Karan V.'][i]}</div><div class="text-muted" style="font-size:12px">${['Medical','Family event','Conference'][i]} · 2 days</div></div>
        <div><button class="btn btn-ghost btn-icon" style="color:var(--success)"><i class="fa-solid fa-check"></i></button>
          <button class="btn btn-ghost btn-icon" style="color:var(--danger)"><i class="fa-solid fa-xmark"></i></button></div></div>`).join('')}
    </div>
  </div>

  <div class="card mt-3">
    <div class="card-title"><h3>Recent attendance log</h3></div>
    <div class="table-wrap"><table class="table">
      <thead><tr><th>Date</th><th>Subject</th><th>Present</th><th>Absent</th><th>Late</th><th>%</th></tr></thead>
      <tbody>${window.DEMO_ATTENDANCE.slice(0,10).map(a=>{
        const tot=a.present+a.absent+a.late; const p=((a.present/tot)*100).toFixed(1);
        return `<tr><td>${a.date}</td><td>${a.subject}</td>
          <td><span class="badge success">${a.present}</span></td>
          <td><span class="badge danger">${a.absent}</span></td>
          <td><span class="badge warning">${a.late}</span></td>
          <td><strong>${p}%</strong></td></tr>`;
      }).join('')}</tbody></table></div>
  </div>`);
new Chart(document.getElementById('attChart'),{type:'line',
  data:{labels:window.DEMO_DASHBOARD.attendance30d.map((_,i)=>i+1),
    datasets:[{label:'%',data:window.DEMO_DASHBOARD.attendance30d,borderColor:'#22C55E',backgroundColor:'rgba(34,197,94,.12)',fill:true,tension:.3,borderWidth:2}]},
  options:{plugins:{legend:{display:false}},scales:{y:{min:70,max:100}}}});
