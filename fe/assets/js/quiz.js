function convertToText() {
    // Lấy tất cả các phần tử có class là "qs-option" và "qs-question"
    var optionLabels = document.querySelectorAll('.qs-option');
    var questionLabels = document.querySelectorAll('.qs-question');

    // Chuyển đổi các câu trả lời từ input thành text
    optionLabels.forEach(function (label) {
        var input = label.querySelector('.qs-option__input');
        var inputValue = input.value;
        var newTextElement = document.createElement('span');
        newTextElement.textContent = inputValue;
        newTextElement.classList.add('qs-option__text');
        label.appendChild(newTextElement);
        input.style.display = 'none';
    });

    // Chuyển đổi các câu hỏi từ input thành text
    questionLabels.forEach(function (label) {
        var input = label.querySelector('.qs-question__input');
        var inputValue = input.value;
        var newTextElement = document.createElement('span');
        newTextElement.textContent = inputValue;
        newTextElement.classList.add('qs-question__text');
        label.appendChild(newTextElement);
        input.style.display = 'none';
    });

    // Ẩn nút "Tạo bài kiểm tra"
    document.querySelector('.test-submit').style.display = 'none';

    // Hiển thị lại nút "Quay lại quiz"
    document.querySelector('.back-to-quiz').style.display = 'block';
}


function backToQuiz() {
    // Lấy tất cả các phần tử có class là "qs-option" và "qs-question"
    var optionLabels = document.querySelectorAll('.qs-option');
    var questionLabels = document.querySelectorAll('.qs-question');

    // Xóa bỏ các phần tử span đã thêm từ text nếu chúng tồn tại
    optionLabels.forEach(function (label) {
        var textElement = label.querySelector('.qs-option__text');
        if (textElement) {
            textElement.remove();
        }
    });

    questionLabels.forEach(function (label) {
        var textElement = label.querySelector('.qs-question__text');
        if (textElement) {
            textElement.remove();
        }
    });

    // Chuyển đổi các câu trả lời từ text thành input
    optionLabels.forEach(function (label) {
        var input = label.querySelector('.qs-option__input');
        input.style.display = 'block';
    });

    // Chuyển đổi các câu hỏi từ text thành input
    questionLabels.forEach(function (label) {
        var input = label.querySelector('.qs-question__input');
        input.style.display = 'block';
    });

    // Hiển thị lại nút "Tạo bài kiểm tra"
    document.querySelector('.test-submit').style.display = 'block';

    // Ẩn nút "Quay lại quiz"
    document.querySelector('.back-to-quiz').style.display = 'none';
}



