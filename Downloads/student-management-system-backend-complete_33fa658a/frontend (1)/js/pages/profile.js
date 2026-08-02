
renderLayout('profile','My Profile',[{label:'Account'},{label:'Profile'}]);
setPageContent(`
  <div class="row">
    <div class="col card" style="max-width:340px;text-align:center">
      <div style="width:120px;height:120px;border-radius:50%;background:linear-gradient(135deg,var(--primary),var(--accent));color:#fff;display:grid;place-items:center;font-size:42px;font-weight:600;margin:0 auto 14px">AS</div>
      <h3>Ananya Sharma</h3>
      <p style="font-size:13px">admin@sms.edu</p>
      <p class="mt-1"><span class="badge info">ADMIN</span></p>
      <button class="btn btn-ghost btn-block mt-2"><i class="fa-solid fa-camera"></i> Change Photo</button>
    </div>
    <div class="col card">
      <div class="card-title"><h3>Personal Information</h3><button class="btn btn-primary"><i class="fa-solid fa-pen"></i> Edit</button></div>
      <div class="row">
        <div class="col field"><label>First Name</label><input class="input" value="Ananya"></div>
        <div class="col field"><label>Last Name</label><input class="input" value="Sharma"></div>
      </div>
      <div class="row">
        <div class="col field"><label>Email</label><input class="input" value="admin@sms.edu"></div>
        <div class="col field"><label>Phone</label><input class="input" value="+91 9876543210"></div>
      </div>
      <div class="row">
        <div class="col field"><label>Date of Birth</label><input class="input" type="date" value="1990-04-12"></div>
        <div class="col field"><label>Gender</label><select class="select"><option>Female</option><option>Male</option><option>Other</option></select></div>
      </div>
      <div class="field"><label>Address</label><textarea class="textarea" rows="3">SMS University Campus, Block A, Room 204, New Delhi 110001</textarea></div>
    </div>
  </div>
  <div class="card mt-3">
    <div class="card-title"><h3>Active sessions</h3></div>
    <div class="table-wrap"><table class="table">
      <thead><tr><th>Device</th><th>Browser</th><th>IP</th><th>Location</th><th>Last seen</th><th></th></tr></thead>
      <tbody>
        <tr><td><i class="fa-solid fa-desktop"></i> MacBook Pro</td><td>Chrome 124</td><td>103.21.x.x</td><td>New Delhi, IN</td><td>Active now</td><td class="text-right"><span class="badge success">Current</span></td></tr>
        <tr><td><i class="fa-solid fa-mobile-screen"></i> iPhone 14</td><td>Safari</td><td>49.36.x.x</td><td>Mumbai, IN</td><td>2 hours ago</td><td class="text-right"><button class="btn btn-ghost" style="color:var(--danger)"><i class="fa-solid fa-power-off"></i> Revoke</button></td></tr>
      </tbody></table></div>
  </div>`);
