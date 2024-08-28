var token = localStorage.getItem('Authorization')

// này là phần cho avatar


// Lấy phần tử cửa sổ thay đổi ảnh đại diện và nút đóng
var modal = document.getElementById("changeAvatarModal");
var closeBtn = document.getElementsByClassName("close")[0];

// Lấy phần tử ảnh đại diện và thêm sự kiện click
var avatar = document.getElementById("avatar");
avatar.addEventListener("click", function() {
  modal.style.display = "block";
});

// Sự kiện đóng cửa sổ
closeBtn.onclick = function() {
  modal.style.display = "none";
}

// Đóng cửa sổ khi click bên ngoài cửa sổ
window.onclick = function(event) {
  if (event.target == modal) {
    modal.style.display = "none";
  }
}

// Hiển thị trước ảnh khi người dùng chọn ảnh từ máy tính
document.getElementById("newAvatar").addEventListener("change", function() {
  var reader = new FileReader();
  reader.onload = function(e) {
    document.getElementById("preview").src = e.target.result;
  };
  reader.readAsDataURL(this.files[0]);
});




// Hàm lưu ảnh đại diện mới
function saveNewAvatar() {
  var newAvatarFile = document.getElementById("newAvatar").files[0];
  
  // Kiểm tra xem người dùng đã chọn ảnh hay chưa
  if (newAvatarFile) {
    var reader = new FileReader();
    reader.onload = function(e) {
      var newAvatarSrc = e.target.result; // Đường dẫn của ảnh mới
      var avatarImg = document.querySelector("#avatar img");
      avatarImg.src = newAvatarSrc; // Cập nhật đường dẫn của ảnh trong class "avatar"
      
      // Lưu đường dẫn của ảnh vào Local Storage
      localStorage.setItem("avatar", newAvatarSrc);
      
      // Xóa ảnh avatar ban đầu khỏi Local Storage (nếu có)
      localStorage.removeItem("defaultAvatar");

      // Đóng cửa sổ sau khi lưu
      modal.style.display = "none";
    };
    reader.readAsDataURL(newAvatarFile);
  } else {
    // Hiển thị thông báo nếu người dùng không chọn ảnh
    alert("Vui lòng chọn một ảnh để thay đổi!");
  }
}





// này là phần cho upload file văn bằng


// Lấy phần tử cửa sổ modal và biểu tượng
var uploadModal = document.getElementById("uploadModal");
var certiIcon = document.querySelector(".bangicon");

// Mở cửa sổ modal khi click vào biểu tượng
function openModal() {
 uploadModal.style.display = "block";
}

// Đóng cửa sổ modal khi click vào nút đóng
function closeModal() {
 uploadModal.style.display = "none";
}

// Hiển thị trước ảnh khi người dùng chọn ảnh từ máy tính
document.getElementById("certificateFile").addEventListener("change", function() {
 var reader = new FileReader();
 reader.onload = function(e) {
    var previewFrame = document.getElementById("previewFrame");
    previewFrame.style.backgroundImage = "url('" + e.target.result + "')";
 };
 reader.readAsDataURL(this.files[0]);
});

// Tải lên văn bằng
function uploadCertificate() {
 var input = document.getElementById('certificateFile');
 var file = input.files[0];
 var fileType = file.type;

 if (fileType.includes('image')) {
    var reader = new FileReader();
    reader.onload = function(e) {
      var img = document.createElement('img');
      img.src = e.target.result;
      img.style.maxWidth = '100%';
      var vanbang = document.querySelector('.vanbang');
      vanbang.innerHTML = '';
      vanbang.appendChild(img);
      // Lưu trạng thái của tệp hình ảnh vào localStorage
      localStorage.setItem('uploadedFile', e.target.result);
      localStorage.setItem('fileType', 'image');
    }
    reader.readAsDataURL(file);
 } else if (fileType === 'application/pdf') {
    var reader = new FileReader();
    reader.onload = function(e) {
      var pdfUrl = e.target.result;
      var iframe = document.createElement('iframe');
      iframe.src = pdfUrl;
      iframe.style.width = '100%';
      iframe.style.height = '100%';
      iframe.style.border = 'none';
      var vanbang = document.querySelector('.vanbang');
      vanbang.innerHTML = '';
      vanbang.appendChild(iframe);
      var downloadLink = document.getElementById('downloadLink');
      downloadLink.style.display = 'block';
      var downloadButton = document.getElementById('downloadButton');
      downloadButton.href = pdfUrl;
      downloadButton.download = 'certificate.pdf';
      // Lưu trạng thái của tệp PDF vào localStorage
      localStorage.setItem('uploadedFile', pdfUrl);
      localStorage.setItem('fileType', 'pdf');
    }
    reader.readAsDataURL(file);
 } else {
    alert('Định dạng tệp không được hỗ trợ.');
 }
 closeModal();
}

// Hàm này sẽ được gọi khi trang được tải lại
function restoreUploadedFile() {
 var uploadedFile = localStorage.getItem('uploadedFile');
 var fileType = localStorage.getItem('fileType');
 if (uploadedFile) {
    if (fileType === 'image') {
      var img = document.createElement('img');
      img.src = uploadedFile;
      img.style.maxWidth = '100%';
      var vanbang = document.querySelector('.vanbang');
      vanbang.innerHTML = '';
      vanbang.appendChild(img);
    } else if (fileType === 'pdf') {
      var iframe = document.createElement('iframe');
      iframe.src = uploadedFile;
      iframe.style.width = '100%';
      iframe.style.height = '100%';
      iframe.style.border = 'none';
      var vanbang = document.querySelector('.vanbang');
      vanbang.innerHTML = '';
      vanbang.appendChild(iframe);
      var downloadLink = document.getElementById('downloadLink');
      downloadLink.style.display = 'block';
      var downloadButton = document.getElementById('downloadButton');
      downloadButton.href = uploadedFile;
      downloadButton.download = 'certificate.pdf';
    }
 }
}

// Gọi hàm restoreUploadedFile khi DOM của trang đã được tải hoàn toàn
document.addEventListener('DOMContentLoaded', function() {
 restoreUploadedFile();
});






// phần sửa thông tin 

// Open edit modal
function openEditForm() {
  // Display edit modal
  var modal = document.getElementById('editModal');
  modal.style.display = 'block';
}

// Close edit modal
function closeEditModal() {
  var modal = document.getElementById('editModal');
  modal.style.display = 'none';
}

// Save edited information
function saveInfo() {

  var form = document.getElementById('infoForm');

  let url = 'http://localhost:8080/teacher/id/info?'
  var fullname = form.elements['fullname'].value;
  var birthdate = form.elements['birthdate'].value;
  var khoa = form.elements['khoa'].value;
  var dienthoai = form.elements['dienthoai'].value;
  var email = form.elements['email'].value;
  var hocvi = form.elements['hocvi'].value;
  var chuyennganh = form.elements['cn'].value;
  var truong = form.elements['truong'].value;
  let [year, month, day] = birthdate.split('-');
  fetch(url + new URLSearchParams({
    name: fullname,
    falcuty: khoa,
    phoneNumber:dienthoai,
    email:email,
    phd: hocvi,
    university: truong,
    master: chuyennganh,
    year: Number(year),
    month: Number(month),
    date: Number(day)
  }), {
    method: 'PUT',
    mode: 'cors',
    headers: {
      'Authorization': token
    }
  }).then(respone => {
    if (!respone.ok) throw new Error(respone.statusText)
      return respone.json();
  }).then (data => {
    location.reload();
  })
  
  // Close edit modal
  closeEditModal();

  // Reload the page to display the updated information
  // location.reload();
}

// Load edited information from database when the page is loaded
document.addEventListener('DOMContentLoaded', function() {
  getInfo().then(data => {
    addinner(data);
  })
  
});


        // Đổi tên sinh viên 
        // Restore form data when the page is loaded
        window.addEventListener('DOMContentLoaded', function() {
          // Lấy thông tin từ local storage
          var currentName = localStorage.getItem('teacherName') || "";

          // Hiển thị thông tin từ local storage trên màn hình chính
          document.querySelector('.name').innerText = currentName;
        });

      // Function to open edit modal
      function updateName() {
        var modal = document.getElementById("changeName");
        modal.style.display = "block";

        // Lấy tên sinh viên hiện tại từ local storage (nếu có)
        var currentName = localStorage.getItem('teacherName') || "";

        // Đặt giá trị hiện tại của tên sinh viên vào input
        document.getElementById('editTeacherName').value = currentName;
      }

      // Lắng nghe sự kiện submit form
      document.getElementById('editFormName').addEventListener('submit', function(event) {
        event.preventDefault(); // Ngăn chặn hành vi mặc định của form

        // Lấy giá trị từ input
        var newName = document.getElementById('editTeacherName').value;

        // Lưu giá trị của tên sinh viên vào local storage
        localStorage.setItem('teacherName', newName);

        // Hiển thị tên sinh viên mới trên màn hình chính
        document.querySelector('.name').innerText = newName;

        // Ẩn modal sau khi submit
        closeModalName();
      });

      // Function to close edit modal
      function closeModalName() {
        var modal = document.getElementById("changeName");
        modal.style.display = "none";
      }


    // đổi gmail của giáo viên

    // Restore form data when the page is loaded
    window.addEventListener('DOMContentLoaded', function() {
      // Lấy thông tin từ local storage
      var currentEmail = localStorage.getItem('teachEmail') || "";

      // Hiển thị thông tin từ local storage trên màn hình chính
      document.querySelector('.teachermail').innerText = currentEmail;
  });
  // Function to open email edit modal
  function updateEmail() {
      var modal = document.getElementById("changeEmail");
      modal.style.display = "block";

      // Lấy email hiện tại từ local storage (nếu có)
      var currentEmail = localStorage.getItem('teachEmail') || "";

      // Đặt giá trị hiện tại của gmail vào input
      document.getElementById('editTeacherEmail').value = currentEmail;
  }

  // Lắng nghe sự kiện submit form
  document.getElementById('editFormEmail').addEventListener('submit', function(event) {
      event.preventDefault(); // Ngăn chặn hành vi mặc định của form

      // Lấy giá trị từ input
      var newEmail = document.getElementById('editTeacherEmail').value;

      // Lưu giá trị của tên sinh viên và mã số sinh viên vào local storage
      localStorage.setItem('teachEmail', newEmail);

      // Hiển thị tên sinh viên và mã số sinh viên mới trên màn hình chính
      document.querySelector('.teachermail').innerText = newEmail;

      // Ẩn modal sau khi submit
      closeModalEmail();
  });

  // Function to close edit modal
  function closeModalEmail() {
      var modal = document.getElementById("changeEmail");
      modal.style.display = "none";
  }



  async function getInfo(){
    let url = 'http://localhost:8080/teacher/id';

    let returnVal = await fetch(url , {
      method: 'GET',
      mode: 'cors',
      headers: {
        'Authorization': token
      }
    }).then(res => {
      if (!res.ok) throw new Error(res.statusText);

      return res.json();
    }).then(data => {
      return data;
    });

    return returnVal;
  }

  function addinner(data) {
    var fullname = data.name;
    var birthdate = new Date(data.dayofBirth.seconds * 1000);
    var date = birthdate.toLocaleDateString();
    var khoa = data.falcuty;
    var dienthoai = data.phoneNumber;
    var email = data.email;
    var hocvi = data.certificate.phd;
    var cn = data.certificate.master;
    var daymh = data.courseID;
  
    var personalInfo = document.getElementById('personalInfo');
  
    personalInfo.innerHTML = '<li><strong>Họ và tên:</strong> ' + fullname + '</li>' +
                              '<li><strong>Ngày tháng năm sinh:</strong> ' + date + '</li>' +
                              '<li><strong>Khoa:</strong> ' + khoa + '</li>' +
                              '<li><strong>Điện thoại liên hệ:</strong> ' + dienthoai + '</li>' +
                              '<li><strong>Email:</strong> ' + email + '</li>' +
                              '<li><strong>Học vị:</strong> ' + hocvi + '</li>' +
                              '<li><strong>Chuyên ngành:</strong> ' + cn + '</li>' +
                              '<li><strong>Giảng dạy các môn học đại học:</strong> ' + daymh + '</li>' 
                              ;
    var savedAvatar = localStorage.getItem("avatar");
    if (savedAvatar) {
      // Nếu có, cập nhật ảnh avatar
      var avatarImg = document.querySelector("#avatar img");
      avatarImg.src = savedAvatar;
    }
  }