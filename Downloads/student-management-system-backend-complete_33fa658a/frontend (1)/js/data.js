/* data.js — demo sample data so every page renders even without the backend running */

window.DEMO_DASHBOARD = {
  totalStudents: 2840, totalTeachers: 142, departments: 8, courses: 24,
  todayAttendance: 92.4, revenueMonth: 1284500, pendingFees: 312000, upcomingExams: 6,
  recentAdmissions: [
    { name: 'Ravi Mehta',    course: 'B.Tech CSE',  date: '2026-06-22' },
    { name: 'Aisha Khan',    course: 'B.Tech ECE',  date: '2026-06-22' },
    { name: 'Karan Verma',   course: 'BBA',         date: '2026-06-21' },
    { name: 'Sneha Patel',   course: 'B.Tech ME',   date: '2026-06-20' },
    { name: 'Mohit Saini',   course: 'M.Tech CSE',  date: '2026-06-19' },
  ],
  admissions12m: [120, 145, 132, 168, 190, 175, 210, 240, 230, 260, 285, 312],
  attendance30d: Array.from({length:30}, (_,i)=> 85 + Math.round(Math.sin(i/3)*6 + Math.random()*4)),
  revenue12m:   [80, 95, 110, 125, 132, 140, 138, 142, 150, 158, 162, 128].map(v=>v*1000),
};

const FIRSTS = ['Ravi','Aisha','Karan','Sneha','Mohit','Priya','Arjun','Nisha','Rahul','Anaya','Vikram','Pooja','Sahil','Rhea','Manish','Tara','Aditya','Isha','Rohan','Meera'];
const LASTS  = ['Mehta','Khan','Verma','Patel','Saini','Sharma','Iyer','Reddy','Nair','Joshi','Kapoor','Gupta','Singh','Das','Chopra'];
const DEPTS  = ['CSE','ECE','ME','CE','EE','IT','MBA','MCA'];
const COURSES = ['B.Tech CSE','B.Tech ECE','B.Tech ME','MBA','BCA','MCA'];
const STATUS = ['ACTIVE','ACTIVE','ACTIVE','INACTIVE','GRADUATED'];

function rand(arr) { return arr[Math.floor(Math.random()*arr.length)]; }

window.DEMO_STUDENTS = Array.from({length: 84}, (_, i) => {
  const fn = rand(FIRSTS), ln = rand(LASTS);
  const dept = rand(DEPTS);
  return {
    id: i+1,
    rollNumber: `${dept}-2026-${String(i+1).padStart(3,'0')}`,
    admissionNumber: `ADM${2026000 + i + 1}`,
    firstName: fn, lastName: ln,
    fullName: `${fn} ${ln}`,
    email: `${fn.toLowerCase()}.${ln.toLowerCase()}${i}@sms.edu`,
    phone: `+91 9${Math.floor(100000000 + Math.random()*900000000)}`,
    department: dept,
    course: rand(COURSES),
    semester: 1 + (i % 8),
    status: rand(STATUS),
    cgpa: (6 + Math.random()*4).toFixed(2),
  };
});

window.DEMO_TEACHERS = Array.from({length: 28}, (_, i) => {
  const fn = rand(FIRSTS), ln = rand(LASTS);
  return {
    id: i+1, employeeCode: `EMP-${1000+i}`,
    fullName: `${fn} ${ln}`, email: `${fn.toLowerCase()}.${ln.toLowerCase()}@sms.edu`,
    phone: `+91 9${Math.floor(100000000 + Math.random()*900000000)}`,
    department: rand(DEPTS),
    qualification: rand(['Ph.D','M.Tech','M.Sc','MBA']),
    experience: 1 + Math.floor(Math.random()*22),
    salary: 40000 + Math.floor(Math.random()*60000),
    status: 'ACTIVE'
  };
});

window.DEMO_COURSES = [
  { id:1, code:'CSE-BT', name:'B.Tech Computer Science', credits:160, duration:8, department:'CSE' },
  { id:2, code:'ECE-BT', name:'B.Tech Electronics',      credits:160, duration:8, department:'ECE' },
  { id:3, code:'ME-BT',  name:'B.Tech Mechanical',       credits:160, duration:8, department:'ME'  },
  { id:4, code:'MBA',    name:'Master of Business Admin',credits:96,  duration:4, department:'MBA' },
  { id:5, code:'BCA',    name:'Bachelor of Computer App',credits:120, duration:6, department:'CSE' },
  { id:6, code:'MCA',    name:'Master of Computer App',  credits:120, duration:4, department:'MCA' },
];

window.DEMO_SUBJECTS = Array.from({length: 18}, (_, i) => ({
  id: i+1,
  code: `SUB-${100+i}`,
  name: ['Data Structures','Algorithms','Operating Systems','Database Systems','Computer Networks','Machine Learning','Cloud Computing','Digital Logic','Microprocessors','Thermodynamics','Fluid Mechanics','Strength of Materials','Organizational Behavior','Marketing Management','Financial Accounting','Software Engineering','Web Technologies','Compiler Design'][i],
  credits: 3 + (i%2),
  semester: 1 + (i%8),
  course: rand(COURSES),
  teacher: `${rand(FIRSTS)} ${rand(LASTS)}`
}));

window.DEMO_ATTENDANCE = Array.from({length: 30}, (_, i) => ({
  date: new Date(Date.now() - i*86400000).toISOString().slice(0,10),
  subject: rand(window.DEMO_SUBJECTS).name,
  present: 80 + Math.floor(Math.random()*20),
  absent:  Math.floor(Math.random()*8),
  late:    Math.floor(Math.random()*4),
}));

window.DEMO_EXAMS = [
  { id:1, name:'Mid-Sem Spring 2026', type:'MID', course:'B.Tech CSE', semester:4, start:'2026-07-10', end:'2026-07-18' },
  { id:2, name:'End-Sem Spring 2026', type:'END', course:'B.Tech CSE', semester:4, start:'2026-08-15', end:'2026-08-28' },
  { id:3, name:'Practical Eval',       type:'PRACTICAL', course:'B.Tech ECE', semester:6, start:'2026-07-22', end:'2026-07-24' },
  { id:4, name:'Quiz #2',              type:'QUIZ', course:'MBA', semester:2, start:'2026-07-05', end:'2026-07-05' },
];

window.DEMO_RESULTS = window.DEMO_STUDENTS.slice(0, 30).map(s => ({
  rollNumber: s.rollNumber, name: s.fullName, semester: s.semester,
  sgpa: (5 + Math.random()*4).toFixed(2),
  cgpa: s.cgpa,
  status: Math.random() > .1 ? 'PASS' : 'FAIL'
}));

window.DEMO_FEES = window.DEMO_STUDENTS.slice(0, 30).map(s => {
  const total = 60000 + Math.floor(Math.random()*40000);
  const paid  = Math.random() > .35 ? total : Math.floor(total * Math.random());
  return { rollNumber: s.rollNumber, name: s.fullName, course: s.course, total, paid, due: total - paid, status: paid === total ? 'PAID' : (paid === 0 ? 'PENDING' : 'PARTIAL') };
});

window.DEMO_NOTICES = [
  { id:1, title:'Mid-Semester Examination Schedule Released', audience:'ALL', pinned:true, publishAt:'2026-06-20', author:'Registrar' },
  { id:2, title:'Library Closed for Stocktaking — June 28', audience:'ALL', pinned:false, publishAt:'2026-06-18', author:'Librarian' },
  { id:3, title:'Placement Drive: Infosys on July 5', audience:'STUDENT', pinned:true, publishAt:'2026-06-17', author:'TPO' },
  { id:4, title:'Faculty Meeting on June 26 at 4 PM', audience:'TEACHER', pinned:false, publishAt:'2026-06-15', author:'Dean' },
  { id:5, title:'Sports Day Registrations Open', audience:'STUDENT', pinned:false, publishAt:'2026-06-12', author:'Sports Committee' },
];

const DAYS = ['Mon','Tue','Wed','Thu','Fri','Sat'];
const SLOTS = ['09:00–10:00','10:00–11:00','11:15–12:15','12:15–13:15','14:00–15:00','15:00–16:00'];
window.DEMO_TIMETABLE = DAYS.map(d => ({
  day: d,
  slots: SLOTS.map((s, i) => ({
    time: s, subject: rand(window.DEMO_SUBJECTS).name, teacher: `${rand(FIRSTS)} ${rand(LASTS)}`, room: `R-${100 + Math.floor(Math.random()*40)}`
  }))
}));
