
localStorage.setItem('sms.user',JSON.stringify({name:'Dr. Priya Iyer',email:'priya.iyer@sms.edu',role:'TEACHER'}));
renderLayout('dashboard','Good morning, Dr. Priya',[{label:'Home'},{label:'My dashboard'}]);
setPageContent(`
  <div class="stat-grid mb-3">
    <div class="stat-card"><div class="icon"><i class="fa-solid fa-users"></i></div><div><div class="label">My students</div><div class="value">128</div></div></div>
    <div class="stat-card accent"><div class="icon"><i class="fa-solid fa-book"></i></div><div><div class="label">Subjects</div><div class="value">3</div></div></div>
    <div class="stat-card success"><div class="icon"><i class="fa-solid fa-clipboard-check"></i></div><div><div class="label">Classes today</div><div class="value">4</div></div></div>
    <div class="stat-card warning"><div class="icon"><i class="fa-solid fa-file-pen"></i></div><div><div class="label">Pending marks entry</div><div class="value">12</div></div></div>
  </div>
  <div class="row">
    <div class="col card" style="flex:2"><div class="card-title"><h3>Today's schedule</h3></div>
      <div class="table-wrap"><table class="table">
        <thead><tr><th>Time</th><th>Subject</th><th>Room</th><th>Attendance</th></tr></thead>
        <tbody>${window.DEMO_TIMETABLE[1].slots.slice(0,4).map(s=>`
          <tr><td><strong>${s.time}</strong></td><td>${s.subject}</td><td>${s.room}</td>
          <td><button class="btn btn-primary" style="padding:6px 12px"><i class="fa-solid fa-pen-to-square"></i> Mark</button></td></tr>`).join('')}
        </tbody></table></div>
    </div>
    <div class="col card"><div class="card-title"><h3>Pending leave approvals</h3></div>
      ${[1,2,3].map(i=>`<div class="flex-between" style="padding:10px 0;border-bottom:1px solid var(--border)">
        <div><div style="font-weight:500">${rand(['Ravi','Sneha','Karan'])} ${rand(['M','P','V'])}.</div><div class="text-muted" style="font-size:12px">${rand(['Medical','Family','Personal'])} · 2 days</div></div>
        <div><button class="btn btn-ghost btn-icon" style="color:var(--success)"><i class="fa-solid fa-check"></i></button>
          <button class="btn btn-ghost btn-icon" style="color:var(--danger)"><i class="fa-solid fa-xmark"></i></button></div></div>`).join('')}
    </div>
  </div>`);
function rand(a){return a[Math.floor(Math.random()*a.length)];}
