package com.project.backend.QuizMain.QA;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Question {
    private String question;
    private Answer answer;
    protected Question(){

    }

    public Question(String question, Answer answer) {
        this.question = question;
        this.answer = answer;
    }
}
