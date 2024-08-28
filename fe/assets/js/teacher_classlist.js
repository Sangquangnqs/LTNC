var token = localStorage.getItem('Authorization');

function openliststudent() {
    var url = "list_stu.html";

    const detail = document.querySelectorAll('.card');
    detail.forEach(function(search){
        search.addEventListener('click', function() {
            localStorage.idCourse = search.id;
            window.open(url, "_self");
        });
    });
}

function openCourseDetail(){
    var url = "course-detail.html";

    const detail = document.querySelectorAll('.card');
    detail.forEach(function(search){
        search.addEventListener('click', function() {
            localStorage.idCourse = search.id;
            window.open(url, "_self");
        });
    });
}
getClass();


async function getClass() {
    let url = 'http://localhost:8080/course/all';

    let returnVal = await fetch(url, {
        mode: 'cors',
        method: 'GET',
        headers: {
            'Authorization': token
        }
    }).then(data => {
        if (!data.ok) throw new Error(data.statusText);

        return data.json();
    }).then(data => {

        addinner(data);
        return data;

    });
}


function addinner(data) {
    // home and menu1
    let home = document.getElementById('home');
    let innerHome = home.innerHTML;
    let classHome = 
        `<div class="cot col-lg-4">
            <div class="card" id={idCourse1}>
                <img class="card-img-top" src="./assets/img/teacher/anh3c.jpg" alt="Card image">
                <div class="card-body">
                    <h4 class="card-title">{nameCourse}</h4>
                    <p class="card-text">Mã môn học - {idCourse2} - CQ232</p>
                    <button  class="btn btn-primary card-link" onclick="openCourseDetail()">ĐIỀU CHỈNH KHÓA HỌC</button>
                    <p></p>
                    <button  class="btn btn-primary card-link" onclick="openliststudent()">DANH SÁCH LỚP</button>
                </div>
            </div>
        </div>`;

    let list = document.getElementById('list');

    let classMenu = `<div class="row thelop" onclick="openliststudent()">
                        <div class="col-md-3">
                            <img class="picture" src="./assets/img/teacher/anh3c.jpg" alt="Card image">
                        </div>
                        <div class="col-md-9 vertical-center">
                            <div class="text-center">
                                <h4 style="text-transform: uppercase; font-size:25px;">{nameCourse} {idCourse1} </h4>
                                <h4>Mã môn học - {idCourse2} - CQ232 </h4>
                            </div>
                        </div>
                    </div>`;
    let innerMenu = list.innerHTML;
    for (let i = 0 ; i < data.length ; i++) {
        let course = data[i];
        
        let tempHome = classHome;
        tempHome = tempHome.replace('{idCourse1}', course.id);
        tempHome = tempHome.replace('{idCourse2}', course.id);
        tempHome = tempHome.replace('{nameCourse}', course.name);
        innerHome += tempHome;


        let tempMenu1 = classMenu;
        tempMenu1 = tempMenu1.replace('{nameCourse}', course.name);
        tempMenu1 = tempMenu1.replace('{idCourse1}', course.id);
        tempMenu1 = tempMenu1.replace('{idCourse2}', course.id);
        innerMenu+= tempMenu1;
    }
    home.innerHTML = innerHome;
    list.innerHTML = innerMenu;
}