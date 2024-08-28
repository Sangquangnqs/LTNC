var token = localStorage.getItem('Authorization');
async function getInfo() {
    let url = 'http://localhost:8080/student/id';

    let returnVal = await fetch(url, {
        method: 'GET',
        mode: 'cors',
        headers: {
            'Authorization': token
        }
    }).then(respone => {
        if (!respone.ok) throw Error(respone.statusText);
        console.clear;
        return respone.json();
    }).then(data => {
        return data;
    });

    return returnVal;
}
function addinner(data) {
    // let page = document.getElementsByClassName("contents");
    let page = document.getElementById("nameCourse");

    let nameCourse_html = `
                    {nameCourse}
               `;

    let nameCourse = localStorage.nameCourse;
    nameCourse_html = nameCourse_html.replace('{nameCourse}', nameCourse);
    page.innerHTML = nameCourse_html;

    let name = document.getElementById("nameStu");

    let nameStu_html = `
    <i class="fa-regular fa-user" style="padding-right: 4px"></i>
    Sinh viên: {nameStu}
               `;

    let nameStu = data.name;
    nameStu_html = nameStu_html.replace('{nameStu}', nameStu);
    name.innerHTML = nameStu_html;


}
document.addEventListener('DOMContentLoaded', getInfo().then(data => {
    addinner(data);

}));