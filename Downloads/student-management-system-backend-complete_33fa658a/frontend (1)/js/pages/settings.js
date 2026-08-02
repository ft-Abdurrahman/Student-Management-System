
renderLayout('settings','Settings',[{label:'Account'},{label:'Settings'}]);
setPageContent(`
  <div class="row">
    <div class="col" style="max-width:240px">
      <div class="card" style="padding:8px">
        ${[['Appearance','fa-palette'],['Notifications','fa-bell'],['Security','fa-shield-halved'],['Integrations','fa-plug'],['Billing','fa-credit-card']].map((s,i)=>`
          <a href="#" class="${i===0?'active':''}" style="display:flex;gap:10px;padding:10px 12px;border-radius:8px;font-size:14px;color:${i===0?'var(--primary)':'var(--text-muted)'};background:${i===0?'var(--primary-50)':'transparent'};text-decoration:none">
            <i class="fa-solid ${s[1]}"></i> ${s[0]}</a>`).join('')}
      </div>
    </div>
    <div class="col card">
      <h3 style="margin-bottom:6px">Appearance</h3>
      <p style="margin-bottom:18px">Customize how SMS looks on your device.</p>

      <div class="flex-between mb-3" style="padding-bottom:18px;border-bottom:1px solid var(--border)">
        <div><strong>Theme</strong><p style="font-size:13px">Light, dark, or follow system.</p></div>
        <div class="tabs" style="margin:0;width:auto">
          <button data-theme="light" class="active">Light</button>
          <button data-theme="dark">Dark</button>
          <button data-theme="system">System</button>
        </div>
      </div>

      <div class="flex-between mb-3" style="padding-bottom:18px;border-bottom:1px solid var(--border)">
        <div><strong>Density</strong><p style="font-size:13px">Compact tables fit more rows on screen.</p></div>
        <select class="select" style="width:auto"><option>Comfortable</option><option>Compact</option></select>
      </div>

      <div class="flex-between mb-3" style="padding-bottom:18px;border-bottom:1px solid var(--border)">
        <div><strong>Language</strong><p style="font-size:13px">Used across the dashboard.</p></div>
        <select class="select" style="width:auto"><option>English</option><option>हिन्दी</option><option>Español</option></select>
      </div>

      <div class="flex-between">
        <div><strong>Email notifications</strong><p style="font-size:13px">Receive summaries and alerts.</p></div>
        <label class="switch" style="position:relative;display:inline-block;width:46px;height:24px">
          <input type="checkbox" checked style="opacity:0">
          <span style="position:absolute;inset:0;background:var(--primary);border-radius:24px;cursor:pointer"></span>
          <span style="position:absolute;top:2px;left:24px;width:20px;height:20px;background:#fff;border-radius:50%;transition:.2s"></span>
        </label>
      </div>
    </div>
  </div>`);
document.querySelectorAll('[data-theme]').forEach(b=>b.onclick=()=>{
  document.querySelectorAll('[data-theme]').forEach(x=>x.classList.remove('active'));
  b.classList.add('active');
  if(b.dataset.theme!=='system'){ localStorage.setItem('sms.theme',b.dataset.theme); applyTheme(b.dataset.theme); }
});
