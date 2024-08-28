var token = localStorage.getItem('Authorization');
let storage = `<div class="a{name-source}">
                    <div class="display-info__avt">
                        <div class="ava_nobody"></div>
                        <div class="info-name" style="margin: auto;">
                            <i class="fa-solid fa-graduation-cap" style="margin: auto 5px;"></i>
                            <h2>{fullName}</h2>
                        </div>
                    </div>
                    <div class="display-info__inf">
                        <div class="info-full">
                            <div class="info-full__content">
                                <i class="fa-solid fa-file-signature" style="margin: auto 5px;"></i>
                                <p style="margin: auto 0px;">Họ và tên:</p>
                            </div>
                            <div class="info-full__content1"><p style="margin: 20px 20px;">{fullName1}</p></div>
                        </div>
                        <div class="info-full">
                            <div class="info-full__content">
                                <i class="fa-solid fa-briefcase" style="margin: auto 5px;"></i>
                                <p style="margin: auto 5px;">Chức vụ:</p></div>
                            <div class="info-full__content1"><p style="margin: 20px 20px;">Giảng viên</p></div>
                        </div>
                        <div class="info-full">
                            <div class="info-full__content">
                                <i class="fa-solid fa-book" style="margin: auto 5px;"></i>
                                <p style="margin: auto 5px;">Bộ môn:</p></div>
                            <div class="info-full__content1"><p style="margin: 20px 20px;">{major}</p></div>
                        </div>
                        <div class="info-full">
                            <div class="info-full__content">
                                <i class="fa-solid fa-envelope" style="margin: auto 5px;"></i>
                                <p style="margin: auto 5px;">Email:</p></div>
                            <div class="info-full__content1"><p style="margin: 20px 20px;">{email}</p></div>
                        </div>
                        <div class="info-full">
                            <div class="info-full__content">
                                <i class="fa-solid fa-phone" style="margin: auto 5px;"></i>
                                <p style="margin: auto 5px;">Điện thoại:</p></div>
                            <div class="info-full__content1"><p style="margin: 20px 20px;">{phoneNumber}</p></div>
                        </div>
                        <div class="info-full">
                            <div class="info-full__content">
                                <i class="fa-solid fa-magnifying-glass" style="margin: auto 5px;"></i>
                                <p style="margin: auto 5px;">Nghiên cứu khoa học:</p>
                            </div>
                        </div >
                        <div style="overflow: auto; height: 40%; padding: 0px 10px 10px 30px; ;">
                            <ul>
                                <li>Nghiên cứu xây dựng hệ thống cảnh báo sớm trượt lở đất sử dụng công nghệ GNSS khu vực cao nguyên phục vụ phát triển bền vững kinh tế xã hội</li>
                                <li>Nghiên cứu phân vùng ổn định khu vực bờ sông đồng bằng Sông Cửu Long và các giải pháp bảo vệ bờ </li>
                                <li>Đánh giá tác động môi trường của các dự án phát triển du lịch, điện gió và điện mặt trời vùng đới bờ biển khu vực Khánh Hòa, Bình Thuận, Vũng Tàu và  biển Tây Nam Bộ </li>
                                <li>Xây dựng hệ thống cảnh báo sớm ao xoáy /dòng rip khu vực bãi tắm du lịch khu vực biển Vũng Tàu, Nha Trang</li>
                                <li>Nghiên cứu xây dựng kho ngầm dự trữ năng lượng (dầu, khí hoá lỏng) Dung Quất, Bà Rịa Vũng Tàu</li>
                                <li>Nghiên cứu xây dựng kho ngầm dự trữ an ninh lương thực, thực phẩm (cá, thịt, rau củ quả) vùng Lâm Đồng</li>
                                <li>Nghiên cứu phát triển bền vững các khu vực phát triển kinh tế du lịch đới bờ biển và vùng cao nguyên</li>
                                <li>Nghiên cứu xây dựng bản đồ phân vùng nhạy cảm môi trường khu vực đới bờ biển Việt Nam</li>
                            </ul>
                        </div>
                    </div>
                </div>`;

let box = `<div onclick="changeContent_func(this, '.a{name-source}')"
                class="display-list__item">
                    <div class="item-ava-nobody"></div>
                        <div class="item-name">
                        <h4>{fullName}</h4>
                    </div>
            </div>  `;

getAllTeacher();



async function addinner(data) {
    let teacher_list = document.getElementById('teacher-list');
    let storage_info = document.getElementById('storage-info');
    for (let i = 0; i < data.length; i++) {
        let temp = box;
        let sto = storage;
        sto = sto.replace('{name-source}', data[i].id);
        sto = sto.replace('{fullName}', data[i].name);
        sto = sto.replace('{fullName1}', data[i].name);
        sto = sto.replace('{email}', data[i].email);

        sto = sto.replace('{major}', data[i].certificate.master);
        sto = sto.replace('{phoneNumber}', data[i].phoneNumber);
        
        temp = temp.replace('{name-source}', data[i].id);
        temp = temp.replace('{fullName}', data[i].name);

        storage_info.innerHTML+=sto;
        teacher_list.innerHTML+=temp;
    }

}
async function getAllTeacher() {
    console.log(token);
    let url = 'http://localhost:8080/teacher/all';
    let response = await fetch(url, {
        mode: 'cors',
        method: 'GET',
        headers: {
            'Authorization': token
        }
    }).then (res => {
        if (!res.ok) throw new Error(res.statusText);
        return res.json();
    }).then (data => {
        addinner(data);
        return data;
    });

    return response;
}

var content = document.querySelector('.display-info');
var list_gv=document.querySelector('.display-list').children;



function changeContent_func(p,classname){
    var classnameChange=document.querySelector(classname).innerHTML;
    content.innerHTML=classnameChange;
    for(let i=0;i<list_gv.length;i++){
        if(list_gv[i].classList.contains('active'))
            {
                list_gv[i].classList.remove('active')
            }
            else{}
    }
    p.classList.add('active');
}
