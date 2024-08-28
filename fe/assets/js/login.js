var content2 = document.querySelector('.login');
let count = 0;

function switchToLogin(classname){
    var classnameChange=document.querySelector(classname).innerHTML;
    content2.innerHTML=classnameChange;
    if (classname == '.backToOption') {
        document.getElementById('student-submit').removeEventListener('click', studentLogin);
        document.getElementById('teacher-submit').removeEventListener('click', teacherLogin);
    } else if (classname == '.goToStudent') {
        document.getElementById('student-submit').addEventListener('click', studentLogin);
        document.getElementById('student-submit').removeEventListener('click', studentRegister);
    } else if (classname == '.goToTeacher') {
        document.getElementById('teacher-submit').addEventListener('click', teacherLogin);
        document.getElementById('teacher-submit').removeEventListener('click', teacherRegister);
    } else if (classname == '.signUpStudent') {
        document.getElementById('student-submit').removeEventListener('click', studentLogin);
        document.getElementById('student-submit').addEventListener('click', studentRegister);
    } else if (classname == '.signUpTeacher') {
        document.getElementById('teacher-submit').removeEventListener('click', teacherLogin);
        document.getElementById('teacher-submit').addEventListener('click', teacherRegister);
    } else {
        window.location.href = '/';
    }
}

function studentLogin() {
    document.getElementById('student-submit').removeEventListener('click', studentLogin);
    let studentEmail = document.getElementById('student-email').value.toLowerCase().trim();
    let studentPassword = document.getElementById('student-password').value;
    if (studentEmail == '' || studentPassword == '') {
        document.getElementById('alert-message').innerHTML = 'Vui lòng nhập đầy đủ thông tin';
        document.getElementById('student-submit').addEventListener('click', studentLogin);
        return;
    }
    let form = new FormData();
    form.append('email', studentEmail);
    form.append('password', studentPassword);
    form.append('role', 'STUDENT');
    fetch('http://localhost:8080/login', {
        method: 'POST',
        mode: 'cors',
        body: form
    }).then(
        res => {
            if (!res.ok) throw Error(res.statusText);
            localStorage.setItem('Authorization', res.headers.get('Authorization'));

            return res.json();
        }
    ).then(
        data => {
            localStorage.setItem('Role', data.role);
            window.location.href = '/';
        }
    ).catch (
        err => {
            count++;
            if (count < 3) {
                document.getElementById('student-submit').addEventListener('click', studentLogin);
                document.getElementById('alert-message').innerHTML = 'Tài khoản hoặc mật khẩu không chính xác';
            } else {
                document.getElementById('alert-message').innerHTML = 'Vui lòng thử lại sau 10 phút';
                setTimeout(() => {
                    document.getElementById('student-submit').addEventListener('click', studentLogin);
                    count = 0;
                }, 600000);
            }
            console.clear()
        }
    )
}

function teacherLogin() {
    document.getElementById('teacher-submit').removeEventListener('click', teacherLogin);
    let teacherEmail = document.getElementById('teacher-email').value.toLowerCase().trim();
    let teacherPassword = document.getElementById('teacher-password').value;
    if (teacherEmail === '' || teacherPassword === '') {
        document.getElementById('alert-message').innerHTML = 'Vui lòng nhập đầy đủ thông tin';
        document.getElementById('teacher-submit').addEventListener('click', teacherLogin);
        return;
    }
    let form = new FormData();
    form.append('email', teacherEmail);
    form.append('password', teacherPassword);
    form.append('role', 'TEACHER');
    fetch('http://localhost:8080/login', {
        method: 'POST',
        mode: 'cors',
        body: form
    }).then(
        res => {
            if (!res.ok) throw Error(res.statusText);

            localStorage.setItem('Authorization', res.headers.get('Authorization'));
            return res.json();
        }
    ).then(
        data => {
            localStorage.setItem('Role', data.role);
            window.location.href = '/';
        }
    ).catch(
        err => {
            count++;
            if (count < 3) {
                document.getElementById('teacher-submit').addEventListener('click', teacherLogin);
                document.getElementById('alert-message').innerHTML = 'Tài khoản hoặc mật khẩu không chính xác';
            } else {
                document.getElementById('alert-message').innerHTML = 'Vui lòng thử lại sau 10 phút';
                setTimeout(() => {
                    count = 0;
                    document.getElementById('teacher-submit').addEventListener('click', teacherLogin);
                }, 600000);
            }
            console.clear();
        }
    )
}

function teacherRegister() {
    document.getElementById('teacher-submit').removeEventListener('click', teacherRegister);
    let teacherEmail = document.getElementById('teacher-email').value.toLowerCase().trim();
    let teacherId = document.getElementById('teacher-id').value;
    let teacherPassword = document.getElementById('teacher-password').value;
    if (teacherEmail === '' || teacherPassword === '' || teacherId === '') {
        document.getElementById('alert-message').innerHTML = 'Vui lòng nhập đầy đủ thông tin';
        document.getElementById('teacher-submit').addEventListener('click', teacherRegister);
        return;
    }
    let form = new FormData();
    form.append('email', teacherEmail);
    form.append('id', teacherId);
    form.append('password', teacherPassword);
    form.append('role', 'TEACHER');
    fetch('http://localhost:8080/register', {
        method: 'POST',
        mode: 'cors',
        body: form
    }).then(res => {
        if (!res.ok) throw Error(res.statusText);
        window.location.href = 'login.html';
    }).catch(err => {
        document.getElementById('teacher-submit').addEventListener('click', teacherRegister);
        console.clear();
    })
}

function studentRegister() {
    document.getElementById('student-submit').removeEventListener('click', studentRegister);
    let studentEmail = document.getElementById('student-email').value.toLowerCase().trim();
    let studentId = document.getElementById('student-id').value;
    let studentPassword = document.getElementById('student-password').value;
    if (studentEmail === '' || studentPassword === '' || studentId === '') {
        document.getElementById('alert-message').innerHTML = 'Vui lòng nhập đầy đủ thông tin';
        document.getElementById('student-submit').addEventListener('click', studentRegister);
        return;
    }
    let form = new FormData();
    form.append('email', studentEmail);
    form.append('id', studentId);
    form.append('password', studentPassword);
    form.append('role', 'STUDENT');
    fetch('http://localhost:8080/register', {
        method: 'POST',
        mode: 'cors',
        body: form
    }).then(res => {
        if (!res.ok) throw Error(res.statusText);
        window.location.href = '/login.html';
    }).catch(err => {
        document.getElementById('student-submit').addEventListener('click', studentRegister);
        //console.clear();
    })
}