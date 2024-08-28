var token = localStorage.getItem('Authorization');
getCourse();
let reference = new JSZip();
let slide = new JSZip();
document.addEventListener("DOMContentLoaded", function () {
    var button = document.getElementById("content__toggle");
    var sections = document.querySelectorAll(".content__section");
    var submit_slide = document.getElementById("slide-upload");
    var submit_reference = document.getElementById("reference-upload");
    let id = localStorage.getItem('idCourse');
    submit_slide.addEventListener("click", function () {
        let slide_spinner = document.getElementById("slide-spinner");
        slide_spinner.style.display = "inline-block";
        slide.generateAsync({ type: 'blob' }).then(function (content) {
            let formData = new FormData();
            formData.append('file', content, 'slide.zip');
            fetch(`http://localhost:8080/course/${id}/materials`, {
                method: 'POST',
                mode: 'cors',
                body: formData,
                headers: {
                    'Authorization': token
                }
            }).then(res => {
                if (!res.ok) {
                    throw Error(res.statusText);
                }
            }).catch(err => {
                window.alert("Error: Unable to upload slide");
            }).finally(() => {
                setTimeout(() => {
                    location.reload();
                    slide_spinner.style.display = "none";
                }, 2000);
            })
        })
    });

    submit_reference.addEventListener("click", function () {
        let reference_spinner = document.getElementById("reference-spinner");
        reference_spinner.style.display = "inline-block";
        reference.generateAsync({ type: 'blob' }).then(function (content) {
            let formData = new FormData();
            formData.append('file', content, 'reference.zip');
            fetch(`http://localhost:8080/course/${id}/materials`, {
                method: 'POST',
                mode: 'cors',
                headers: {
                    'Authorization': token
                },
                body: formData
            }).then(res => {
                if (!res.ok) {
                    throw Error(res.statusText);
                }
            }).catch(err => {
                window.alert("Error: Unable to upload reference");
            }).finally(() => {
                setTimeout(() => {
                    location.reload();
                    reference_spinner.style.display = "none";
                }, 2000);
            })
        })
    });
    
    // Thêm sự kiện click cho nút toggle
    button.addEventListener("click", function () {
        var isAllCollapsed = button.textContent === "Thu gọn toàn bộ";
        sections.forEach(function (section) {
            if (isAllCollapsed) {
                collapseSection(section);
            } else {
                expandSection(section);
            }
        });
        // Cập nhật nút toggle
        button.textContent = isAllCollapsed ? "Mở rộng tất cả" : "Thu gọn toàn bộ";
    });

    // Hàm mở rộng section
    function expandSection(section) {
        section.classList.remove("collapsed");
        var overviewImg = section.querySelector(".overview-img img");
        if (overviewImg) {
            overviewImg.style.display = "block"; // Hiển thị hình ảnh
        }
    }

    // Hàm thu gọn section
    function collapseSection(section) {
        section.classList.add("collapsed");
        var overviewImg = section.querySelector(".overview-img img");
        if (overviewImg) {
            overviewImg.style.display = "none"; // Ẩn hình ảnh
        }
    }

    // Thêm sự kiện click cho mỗi heading
    sections.forEach(function (section) {
        var heading = section.querySelector(".section__heading");
        var content = section.querySelector(".section__list"); // Lấy phần nội dung bên dưới mỗi section
        var files = section.querySelectorAll(".file-container"); // Lấy danh sách các file trong section
        heading.addEventListener("click", function () {
            if (section.classList.contains("collapsed")) {
                expandSection(section);
            } else {
                collapseSection(section);
            }
        });
    });

    let teacher_only = document.getElementsByClassName("teacher-only");
    if (localStorage.getItem('Role') === 'STUDENT') {
        for (let i = 0; i < teacher_only.length; i++) {
            teacher_only[i].style.display = 'none';
        }
    }
});


document.addEventListener("DOMContentLoaded", function () {
    // Function to handle file upload
    function handleFileUpload(input, fileList) {

        input.addEventListener("change", function (event) {
            var file = event.target.files[0]; // Lấy danh sách file từ input

            // Hiển thị từng file và tạo một div để chứa file, icon và link mở file
            var fileContainer = document.createElement("div");
            fileContainer.className = "file-container"; // Thêm class cho div chứa file

            var fileIcon = document.createElement("i");
            fileIcon.className = "fa-solid fa-file-arrow-down file-icon"; // Thêm class cho icon

            var fileName = document.createElement("span");
            fileName.textContent = " " + file.name; // Thêm khoảng trắng trước tên file

            // Tạo link để mở file
            var fileLink = document.createElement("a");
            fileLink.href = URL.createObjectURL(file); // Tạo URL cho file
            fileLink.target = "_blank"; // Mở file trong tab mới

            fileLink.appendChild(fileIcon); // Thêm icon vào link
            fileLink.appendChild(fileName); // Thêm tên file vào link

            fileContainer.appendChild(fileLink); // Thêm link vào div chứa file

            JSZipUtils.getBinaryContent(fileLink.href, function (err, content) {
                if (err) {
                    return;
                }
                if (fileList.id === 'slideFileList') {
                    slide.file(file.name, content, { binary: true });
                } else {
                    reference.file(file.name, content, { binary: true });
                }
            })
            // Tạo nút xóa file
            var deleteButton = document.createElement("button");
            deleteButton.textContent = "Xóa";
            deleteButton.className = "delete-button";
            deleteButton.addEventListener("click", function () {
                fileList.removeChild(fileContainer); // Xóa div chứa file khi nút xóa được click
                if (fileList.childNodes.length === 0) {
                    fileList.innerHTML = ''; // Nếu không còn file nào, xóa hết nội dung của danh sách
                }
                if (fileList.id === 'slideFileList') {
                    slide.remove(content, file.name);
                } else {
                    reference.remove(content, file.name);
                }
            });

            fileContainer.appendChild(deleteButton); // Thêm nút xóa vào div chứa file

            fileList.appendChild(fileContainer); // Thêm div chứa file vào danh sách
        });
    }

    var slideFileInput = document.querySelector(".slideFileUpload");
    var referenceFileInput = document.querySelector(".referenceFileUpload");
    var slideFileList = document.getElementById("slideFileList");
    var referenceFileList = document.getElementById("referenceFileList");

    // Xử lý upload file cho cả phần Slide và Tài liệu tham khảo
    handleFileUpload(slideFileInput, slideFileList);
    handleFileUpload(referenceFileInput, referenceFileList);

    //Handle file for reference and slide
    let id = localStorage.getItem('idCourse');
    fetch(`http://localhost:8080/course/${id}/materials`, {
        method: 'GET',
        mode: 'cors',
        headers: {
            'Authorization': token
        }
    }).then(res => {
        if (!res.ok) {
            throw Error(res.statusText);
        }
        return res.json();
    }).then(data => {
        //reference
        var fileContainer = document.createElement("div");
        fileContainer.className = "file-container"; // Thêm class cho div chứa file

        var fileIcon = document.createElement("i");
        fileIcon.className = "fa-solid fa-file-arrow-down file-icon"; // Thêm class cho icon

        var fileName = document.createElement("span");
        fileName.textContent = "reference.zip"; // Thêm khoảng trắng trước tên file

        // Tạo link để mở file
        var fileLink = document.createElement("a");
        fileLink.href = data.reference; // Tạo URL cho file

        fileLink.appendChild(fileIcon); // Thêm icon vào link
        fileLink.appendChild(fileName); // Thêm tên file vào link

        fileContainer.appendChild(fileLink); // Thêm link vào div chứa file

        referenceFileList.appendChild(fileContainer); // Thêm div chứa file vào danh sách

        //slide
        var fileContainer = document.createElement("div");
        fileContainer.className = "file-container"; // Thêm class cho div chứa file

        var fileIcon = document.createElement("i");
        fileIcon.className = "fa-solid fa-file-arrow-down file-icon"; // Thêm class cho icon

        var fileName = document.createElement("span");
        fileName.textContent = "slide.zip"; // Thêm khoảng trắng trước tên file

        // Tạo link để mở file
        var fileLink = document.createElement("a");
        fileLink.href = data.slide; // Tạo URL cho file

        fileLink.appendChild(fileIcon); // Thêm icon vào link
        fileLink.appendChild(fileName); // Thêm tên file vào link

        fileContainer.appendChild(fileLink); // Thêm link vào div chứa file

        slideFileList.appendChild(fileContainer); // Thêm div chứa file vào danh sách
    }).catch(err => {
        console.log(err);
    })
});

function addinner(data) {
    let page = document.getElementById("nameCourse");

    let nameCourse_html = `
                    {nameCourse}
               `;

    let nameCourse = data.name;
    localStorage.nameCourse = data.name;
    nameCourse_html = nameCourse_html.replace('{nameCourse}', nameCourse);
    page.innerHTML = nameCourse_html;


}


async function getCourse() {
    let url = 'http://localhost:8080/course/id?';
    let returnVal = await fetch(url + new URLSearchParams({
        idCourse: localStorage.idCourse
    }), {
        method: "GET",
        mode: "cors",
        headers: {
            'Authorization': token
        }
    }).then(
        data => {
            if (!data.ok) {
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
