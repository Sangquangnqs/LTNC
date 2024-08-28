package com.project.backend.Student;

import java.util.List;

import com.google.cloud.Timestamp;
import com.project.backend.Course.Course;
import com.project.backend.firebase.CollectionName;
import com.project.backend.model.Model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@CollectionName("Student")

@Getter
@Setter
public class Student extends Model {
    private String name;
    private Timestamp dob;
    @NonNull
    private String email;
    private List<Course> CourseID;
    private boolean status;

    private gender gender;
    private String address;
    private String country;
    private String personalId;
    private String phoneNumber;
    private String major;
    protected Student() {}

    public Student(String name, Timestamp dob, @NonNull String email, List<Course> CourseID, boolean status,
                    gender gender, String address, String personalId, String phoneNumber, String major, String country)
     {
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.CourseID = CourseID;
        this.status = false;
        this.gender = gender;
        this.address = address;
        this.personalId = personalId;
        this.phoneNumber = phoneNumber;
        this.major = major;
        this.country = country;
    }
}
