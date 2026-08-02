
renderLayout('fees','Fee Management',[{label:'Operations'},{label:'Fees'}]);
setPageActions(`<button class="btn"><i class="fa-solid fa-file-invoice"></i> Generate Invoices</button>
  <button class="btn btn-primary"><i class="fa-solid fa-plus"></i> Record Payment</button>`);
const inr=n=>'₹'+new Intl.NumberFormat('en-IN').format(n);
setPageContent(`
  <div class="stat-grid mb-2">
    <div class="stat-card success"><div class="icon"><i class="fa-solid fa-circle-check"></i></div><div><div class="label">Collected (Month)</div><div class="value">${inr(1284500)}</div></div></div>
    <div class="stat-card warning"><div class="icon"><i class="fa-solid fa-hourglass-half"></i></div><div><div class="label">Pending</div><div class="value">${inr(312000)}</div></div></div>
    <div class="stat-card danger"><div class="icon"><i class="fa-solid fa-triangle-exclamation"></i></div><div><div class="label">Overdue</div><div class="value">${inr(86000)}</div></div></div>
    <div class="stat-card"><div class="icon"><i class="fa-solid fa-receipt"></i></div><div><div class="label">Receipts (Month)</div><div class="value">218</div></div></div>
  </div>
  <div class="row mb-3">
    <div class="col card chart-card" style="flex:2"><div class="card-title"><h3>Collection trend</h3></div><canvas id="feeChart"></canvas></div>
    <div class="col card"><div class="card-title"><h3>Fee categories</h3></div>
      ${[['Tuition',60],['Hostel',18],['Library',7],['Lab',9],['Misc',6]].map(([n,p])=>`
        <div style="margin-bottom:10px"><div class="flex-between" style="font-size:13px"><span>${n}</span><span>${p}%</span></div>
          <div style="height:6px;background:var(--surface-2);border-radius:3px;overflow:hidden"><div style="width:${p}%;height:100%;background:var(--primary)"></div></div></div>`).join('')}
    </div>
  </div>
  <div class="card">
    <div class="card-title"><h3>Student fee status</h3></div>
    <div class="table-wrap"><table class="table">
      <thead><tr><th>Roll No.</th><th>Student</th><th>Course</th><th>Total</th><th>Paid</th><th>Due</th><th>Status</th><th></th></tr></thead>
      <tbody>${window.DEMO_FEES.map(f=>`<tr>
        <td><strong>${f.rollNumber}</strong></td><td>${f.name}</td><td>${f.course}</td>
        <td>${inr(f.total)}</td><td>${inr(f.paid)}</td><td>${inr(f.due)}</td>
        <td><span class="badge ${f.status==='PAID'?'success':f.status==='PENDING'?'danger':'warning'}">${f.status}</span></td>
        <td class="text-right"><button class="btn btn-ghost"><i class="fa-solid fa-receipt"></i></button>
          ${f.status!=='PAID'?'<button class="btn btn-primary" style="padding:6px 12px"><i class="fa-solid fa-credit-card"></i> Pay</button>':''}</td></tr>`).join('')}
      </tbody></table></div>
  </div>`);
new Chart(document.getElementById('feeChart'),{type:'bar',
  data:{labels:['Jul','Aug','Sep','Oct','Nov','Dec','Jan','Feb','Mar','Apr','May','Jun'],
    datasets:[{label:'Collected',data:window.DEMO_DASHBOARD.revenue12m,backgroundColor:'#2563EB',borderRadius:6}]},
  options:{plugins:{legend:{display:false}}}});
