var token = localStorage.getItem('Authorization');
let adjustStu = `<div class="row">
<div class="thutu col-lg-2">
    <div class="circle">
        <div class="number">{index}</div>
    </div>
</div>
<div class="sinhvien col-lg-6">
    <div class="thestu" style="background-color: #ccf5ff ;">
        <p style=" font-size:20px; text-align: center;"> {nameStudent}</p>
    </div>
</div>
<div class="col-lg-2">
    <div class="thestu" style="background-color: #f0f0f0; margin-top:10px; border-radius:10px; height:30px; padding-top:5px;">
        <p style="font-size:18px; text-align:center;"> {idStudent} </p>
    </div>
    
</div>
<div class="col-lg-2 teacher-only" id="{idStudent2}">
    <button type="button" class="btn btn-info eva" onclick="opennewweb()">Đánh giá</button>
</div>
</div>`;
document.addEventListener('DOMContentLoaded', function(e) {
    
    getClass().then(function(any){
        let teacher_only = document.getElementsByClassName("teacher-only");
        if (localStorage.getItem('Role') === 'STUDENT') {
            for (let i = 0; i < teacher_only.length; i++) {
                teacher_only[i].style.display = 'none';
            }
        }

    })

})


function opennewweb() {
    const detail = document.querySelectorAll('.col-lg-2');
    detail.forEach(function(search){
        search.addEventListener('click', function() {
            localStorage.idStudent = search.id;
            var url = "teacher_evaluate.html";
            window.open(url, "_self");
        });
    });
}

async function getClass() {

    let url = 'http://localhost:8080/course/id?';
    let returnVal = await fetch(url + new URLSearchParams({
        idCourse: localStorage.idCourse
    }), {
        method:"GET",
        mode:"cors",
        headers: {
            'Authorization': token
        }
    }).then(
        data => {
            if(!data.ok) {
                throw Error(data.statusText);
            }
            return data.json();
        }
    ).then(res => {
        return res;
    });
    addinner(returnVal);
    return returnVal;
}
function addinner(data){
    let nameClass = document.getElementById("detail-class");
    let nameClass_html = `<div class="techinf">
                            <p style="padding-top:10px;"> Lớp {nameCourse}</p>
                            <p>L02 - {idCourse}</p>
                        </div>
`;
    let nameCourse = data.name;
    let idCourse = data.id;
    let listStu = data.listStudent;

    for (let i = 0; i < listStu.length; i++) {
        let temp = adjustStu;
        let name = listStu[i].name;
        let id = listStu[i].id;
        temp = temp.replace('{nameStudent}', name);
        temp = temp.replace('{idStudent}', id);
        temp = temp.replace('{idStudent2}', id);
        temp = temp.replace('{index}', i+1);
        nameClass_html += temp;
    }
    nameClass_html = nameClass_html.replace('{nameCourse}', nameCourse);
    nameClass_html = nameClass_html.replace('{idCourse}', idCourse);

    nameClass.innerHTML = nameClass_html;

    document.getElementById('name-Course').innerHTML = nameCourse;
}








