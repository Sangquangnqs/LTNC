package com.project.backend.QuizMain.QA;


import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class QuestionAndAnswer {
    private Question question;

    protected QuestionAndAnswer() {}
    public QuestionAndAnswer(Question question) {
        this.question = question;
    }

}
