package com.project.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.Timestamp;
import com.project.backend.Student.Student;
import com.project.backend.Student.gender;
import com.project.backend.exceptionhandler.ExceptionLog;
import com.project.backend.repository.FirestoreRepository;
import com.project.backend.security.BackendDetailsService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

import com.project.backend.Course.Course;




@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private CourseController courseController;

    @Autowired
    private ExceptionLog exceptionLog;
    @Autowired
    private FirestoreRepository repository;

    // @GetMapping("/Get")
    // public ResponseEntity<Student> getMethodName(
    //     @RequestParam (required = false, defaultValue = "0000") Integer year,
    //     @RequestParam (required = false, defaultValue =  "01") Integer month,
    //     @RequestParam (required = false, defaultValue = "01") Integer day,
    //     @RequestParam (required = false, defaultValue = "00") Integer hour,
    //     @RequestParam (required = false, defaultValue = "00") Integer minute,
    //     @RequestParam (required = false, defaultValue = "00") Integer second
    // ) {
    //     try{
    //         LocalDateTime now = LocalDateTime.of(year, month, day, hour, minute, second);
    //         Timestamp timestamp = convertTimestamp(year, month, day, hour, minute, second);
    //         return timestamp.toString() + " __________________ " + now.toString();

    //     }catch(Exception e){
    //         exceptionLog.log(e);
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    //     }
    //     // LocalDate date = LocalDate.of(2004, 08, 01);
    // }
    

    @PostMapping("/new/id")
    
    public ResponseEntity<Student> setStudent(
                            @RequestParam String id,
                            @RequestParam (required = false, defaultValue = "") String name,
                            @RequestParam(required = false, defaultValue = "") String email,
                            @RequestParam (required = false, defaultValue = "2000") Integer year,
                            @RequestParam (required = false, defaultValue =  "01") Integer month,
                            @RequestParam (required = false, defaultValue = "01") Integer day,
                            @RequestParam (required = false, defaultValue = "00") Integer hour,
                            @RequestParam (required = false, defaultValue = "00") Integer minute,
                            @RequestParam (required = false, defaultValue = "00") Integer second
                            )
    {   
        try{

            if (repository.getDocumentById(Student.class, id) != null){
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }
            List<Course> CourseID = new ArrayList<Course>();
            
            Timestamp dob = convertTimestamp(year, month, day, hour, minute, second);
            Student student = new Student(name, dob, email, CourseID, false, gender.other, "", "", "", "", "");
            repository.saveDocument(student, id);
            return ResponseEntity.ok().body(student);
        } catch (Exception e) {
            exceptionLog.log(e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }

    }

    @PutMapping("/adjustion/id")
    public ResponseEntity<Student> UpdateStudent(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Integer date,
        @RequestParam(required = false) Integer month,
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) gender gender,
        @RequestParam(required = false) String country,
        @RequestParam(required = false) String personalId,
        @RequestParam(required = false) String phoneNumber,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String address,
        @RequestParam(required = false) String major

        
        ) {
        //TODO: process PUT request
        try{
            String id = BackendDetailsService.getCurrentUserId();
            DocumentSnapshot snapshot = repository.getDocumentById(Student.class, id);
            if (snapshot == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Student student = snapshot.toObject(Student.class);

            boolean dobcheck = false;
            if (year == null) dobcheck = true;
            if (name == null || name == "") name = student.getName();
            if (gender == null ) gender = student.getGender();
            if (country == null || country == "") country = student.getCountry();
            if (personalId == null || personalId == "") personalId = student.getPersonalId();
            if (phoneNumber == null || phoneNumber == "") phoneNumber = student.getPhoneNumber();
            if (email == null || email == "") email = student.getEmail();
            if (address == null || address == "") address = student.getAddress();
            if (major == null || major == "") major = student.getMajor();
            
            student.setName(name);

            if (!dobcheck)
                student.setDob(convertTimestamp(year, month, date, 0, 0, 0));
            else 
                student.setDob(student.getDob());
            student.setGender(gender);
            student.setCountry(country);
            student.setPersonalId(personalId);
            student.setPhoneNumber(phoneNumber);
            student.setEmail(email);
            student.setAddress(address);
            student.setMajor(major);
            repository.updateDocumentById(student);
            

            List<Course> courses = student.getCourseID();
            for (int i = 0; i < courses.size(); i++){
                String getidCourse = courses.get(i).getId();
                DocumentSnapshot temp = repository.getDocumentById(Course.class, getidCourse);
                Course findCourse = temp.toObject(Course.class);

                for (int j = 0 ; j < findCourse.getListStudent().size(); j++){
                    if (findCourse.getListStudent().get(j).getId().equals(id)){
                        findCourse.getListStudent().get(j).setName(student.getName());
                        findCourse.getListStudent().get(j).setEmail(student.getEmail());
                        repository.updateDocumentById(findCourse);
                    }
                }
            }

            return ResponseEntity.ok().body(student);

        } catch (Exception e) {
            exceptionLog.log(e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }


    @PutMapping("/adjustion/id/day-of-birth")
    public ResponseEntity<Map<String, Object>> UpdateBirthday(@RequestParam String id,
                                @RequestParam (required = false, defaultValue = "0000") Integer year,
                                @RequestParam (required = false, defaultValue =  "01") Integer month,
                                @RequestParam (required = false, defaultValue = "01") Integer day,
                                @RequestParam (required = false, defaultValue = "00") Integer hour,
                                @RequestParam (required = false, defaultValue = "00") Integer minute,
                                @RequestParam (required = false, defaultValue = "00") Integer second
    ) {
        try {
            DocumentSnapshot snapshot = repository.getDocumentById(Student.class, id);
            if (snapshot == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
    
            Timestamp value = convertTimestamp(year, month, day, hour, minute, second);
            Map<String, Object> changes = snapshot.getData();
            changes.put("dob", value);
            repository.updateDocumentById(Student.class, id, changes);

            
            return ResponseEntity.ok().body(changes);

        } catch (Exception e) {
            exceptionLog.log(e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // @DeleteMapping("id/del")
    // public ResponseEntity<Student> DeleteStudent(@RequestParam String id) {
    //     //TODO: process DELETE request
        
    //     try{            
    //         DocumentSnapshot snapshot = repository.getDocumentById(Student.class, id);
    //         if (snapshot == null)  return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    //         Student student = snapshot.toObject(Student.class);
    //         for (int i = 0; i < student.getCourseID().size(); i++){
    //             String getidCourse = student.getCourseID().get(i).getId();
    //             courseController.deleteStudentinCourse(id, getidCourse);
    //         }
    //         repository.deleteDocumentById(Student.class, id);
    //         return ResponseEntity.ok().body(student);
    //     } catch (Exception e) {
    //         exceptionLog.log(e);
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    //     }
    // }
    @DeleteMapping("/del-all")
    public ResponseEntity<List<Student>> deleteAll() {
        //TODO: process DELETE request
        
        try{
            List<Student> list = new ArrayList<Student>();
            List<DocumentSnapshot> documents = repository.getAllDocuments(Student.class);
            if (documents == null || documents.size() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            for (DocumentSnapshot document : documents) {
                Student teacher = document.toObject(Student.class);
                list.add(teacher);
                
                for (int i = 0; i < teacher.getCourseID().size(); i++){
                    String getid = teacher.getCourseID().get(i).getId();
                    courseController.deleteTeacherinCourse(teacher.getId(), getid);
                }
                
                repository.deleteDocumentById(Student.class, document.getId());
            }
            return ResponseEntity.ok().body(list);
        } catch (Exception e) {
            exceptionLog.log(e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/id")
    public ResponseEntity<Student> getStudentbyId() {
        try {
            String id = BackendDetailsService.getCurrentUserId();
            DocumentSnapshot snapshot = repository.getDocumentById(Student.class, id);
            if (snapshot == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Student student = snapshot.toObject(Student.class);
            return ResponseEntity.ok().body(student);
        } catch (Exception e) {
            exceptionLog.log(e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Student>> getAllStudent() {
        try {
            List<DocumentSnapshot> snapshots = repository.getAllDocuments(Student.class);
            if (snapshots == null || snapshots.size() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            List<Student> students = new ArrayList<Student>();
            for (DocumentSnapshot snapshot : snapshots) {
                Student student = snapshot.toObject(Student.class);
                students.add(student);
            }

            return ResponseEntity.ok().body(students);
        } catch (Exception e) {
            exceptionLog.log(e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    public Timestamp convertTimestamp(Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer second){
        LocalDateTime now = LocalDateTime.of(year, month, day, hour, minute, second);
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        Timestamp timestamp = Timestamp.of(date);
        return timestamp;
    }
}
