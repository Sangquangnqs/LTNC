package com.project.backend.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.cloud.Timestamp;
import com.project.backend.QuizMain.Quizz;
import com.project.backend.firebase.CollectionName;
import com.project.backend.model.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@CollectionName("Course")
public class Course extends Model{
    @Autowired
    private String id;
    private String name; // name of the course
    private Category category;
    private Timestamp startDate;;
    private Timestamp endDate;;
    private Integer price;
    private Map<String,String> timeTable;
    
    private List<NameIDStu> listStudent;
    private List<String> listTeacher;
    // multiple quizzes
    private List<Quizz> listQuizz;
    protected Course(){}

    public Course(
            String id,
            String name, 
            Category category, 
            Timestamp startDate, 
            Timestamp endDate, 
            Integer price, 
            Map<String,String> timeTable,
            List<NameIDStu> listStudent,
            List<String> listTeacher,
            List<Quizz> listQuizz
        ) {
        this.id=id;
        this.name = name;
        this.category = category;
        this.endDate = endDate;
        this.startDate = startDate;
        this.price = price;
        this.timeTable = timeTable;
        this.listStudent = listStudent;
        this.listTeacher = listTeacher;
        this.listQuizz = listQuizz;
    }

}
