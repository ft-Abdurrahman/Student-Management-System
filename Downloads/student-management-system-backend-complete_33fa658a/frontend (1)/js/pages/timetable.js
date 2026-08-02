
renderLayout('timetable','Timetable',[{label:'Academics'},{label:'Timetable'}]);
setPageActions(`<select class="select" style="width:auto"><option>B.Tech CSE - Sem 4</option><option>B.Tech ECE - Sem 6</option></select>
  <button class="btn btn-primary"><i class="fa-solid fa-plus"></i> Add Slot</button>`);
const days=window.DEMO_TIMETABLE;
const slotTimes=days[0].slots.map(s=>s.time);
setPageContent(`
  <div class="card">
    <div class="tt-grid">
      <div></div>${days.map(d=>`<div class="tt-head">${d.day}</div>`).join('')}
      ${slotTimes.map((t,ti)=>`
        <div class="tt-time">${t}</div>
        ${days.map(d=>`<div class="tt-slot"><div class="subject">${d.slots[ti].subject}</div><div class="meta"><i class="fa-solid fa-user"></i> ${d.slots[ti].teacher}<br><i class="fa-solid fa-location-dot"></i> ${d.slots[ti].room}</div></div>`).join('')}
      `).join('')}
    </div>
  </div>`);
