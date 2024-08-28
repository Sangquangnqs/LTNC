package com.project.backend.QuizMain;

import com.google.cloud.Timestamp;
import com.project.backend.QuizMain.QA.QuestionAndAnswer;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
@Getter
@Setter
public class Quizz {
    private Timestamp Startdate;
    private Timestamp Enddate;
    private List<QuestionAndAnswer> questionAndAnswer;
    private String title;
    // detail score of student found by id
    private Map<String,QuizzDetail> info;

    // initialize times to do the quiz
    private Integer times;
    protected Quizz(){}

    public Quizz(
        Timestamp startDate,
        Timestamp endDate,
        List<QuestionAndAnswer> questionAndAnswer,

        // Student ID to QuizzDetail
        Map<String,QuizzDetail> info,
        String title,
        Integer times
        ){
            this.Startdate = startDate;
            this.Enddate = endDate;
            this.questionAndAnswer = questionAndAnswer;
            this.info = info;
            this.title = title;
            this.times = times;
        }
}

