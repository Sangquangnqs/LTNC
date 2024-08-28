package com.project.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.project.backend.Student.Student;
import com.project.backend.Teacher.Certificate;
import com.project.backend.Teacher.Teacher;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;





@RestController
@RequestMapping("/teacher")

public class TeacherController {
    
    @Autowired
    private FirestoreRepository repository;

    @Autowired
    private ExceptionLog exceptionLog;
    
    @Autowired
    private CourseController courseController;
    @GetMapping("/id")
    public ResponseEntity<Teacher> CurrentTeacherInfo() {
        try {
            String id = BackendDetailsService.getCurrentUserId();
            DocumentSnapshot documentSnapshot = repository.getDocumentById(Teacher.class, id);
            if (documentSnapshot == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
    
            Teacher teacher = documentSnapshot.toObject(Teacher.class);
            return ResponseEntity.ok().body(teacher);

        } catch (Exception e) {
            exceptionLog.log(e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<Teacher>> getAllTeacher() {
        try {
            List<DocumentSnapshot> snapshots = repository.getAllDocuments(Teacher.class);
            if (snapshots == null || snapshots.size() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            List<Teacher> teachers = new ArrayList<Teacher>();
            for (DocumentSnapshot snapshot : snapshots) {
                Teacher each = snapshot.toObject(Teacher.class);
                teachers.add(each);
            }

            return ResponseEntity.ok().body(teachers);
        } catch (Exception e) {
            exceptionLog.log(e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }
    @PutMapping("/id/info")
    public ResponseEntity<Teacher> updateCurrentTeacher(
        @RequestParam (required = false, defaultValue ="") String name,
        @RequestParam(required = false, defaultValue = "01") int date,
        @RequestParam(required = false, defaultValue = "01") int month,
        @RequestParam(required = false, defaultValue = "2004") int year,
        @RequestParam (required = false, defaultValue ="") String falcuty,
        @RequestParam (required = false, defaultValue ="") String phoneNumber,
        @RequestParam (required = false, defaultValue ="") String email,
        @RequestParam (required = false, defaultValue ="") String phd,
        @RequestParam (required = false, defaultValue ="") String university,
        @RequestParam (required = false, defaultValue ="") String master) 
    {
        try{
            String id = BackendDetailsService.getCurrentUserId();
            DocumentSnapshot snapshot = repository.getDocumentById(Teacher.class, id);
            if (snapshot == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Teacher teacher = snapshot.toObject(Teacher.class);
            
            if (name == null || name == "") name = teacher.getName();
            if (falcuty == null || falcuty == "") falcuty = teacher.getFalcuty();
            if (phoneNumber == null || phoneNumber == "") phoneNumber = teacher.getPhoneNumber();
            if (email == null || email == "") email = teacher.getEmail();
            if (phd == null || phd == "") phd = teacher.getCertificate().getPhd();
            if (university == null || university == "") university = teacher.getCertificate().getUniversity();
            if (master == null || master == "") master = teacher.getCertificate().getMaster();

            teacher.setName(name);
            Certificate certificate = new Certificate(master, phd, university);
            teacher.setCertificate(certificate);
            teacher.setFalcuty(falcuty);
            teacher.setPhoneNumber(phoneNumber);
            teacher.setEmail(email);
            teacher.setDayofBirth(convertTimestamp(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(date), 00, 00, 00));
            repository.updateDocumentById(teacher);
            return ResponseEntity.ok().body(teacher);
        } catch (Exception e) {
            exceptionLog.log(e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

    @PutMapping("/id/info/day-of-birth")
    public ResponseEntity<Map<String,Object>> updateDOB(
        @RequestParam String id, 
        @RequestParam (required = false, defaultValue = "0000") Integer year,
        @RequestParam (required = false, defaultValue =  "01") Integer month,
        @RequestParam (required = false, defaultValue = "01") Integer day,
        @RequestParam (required = false, defaultValue = "00") Integer hour,
        @RequestParam (required = false, defaultValue = "00") Integer minute,
        @RequestParam (required = false, defaultValue = "00") Integer second
        ) {
        try{
            DocumentSnapshot snapshot = repository.getDocumentById(Teacher.class, id);
            if (snapshot == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Map<String, Object> obj = snapshot.getData();
            Timestamp value = convertTimestamp(year, month, day, hour, minute, second);
            obj.put("dob", value);
            repository.updateDocumentById(Teacher.class, id, obj);

            return ResponseEntity.ok().body(obj);
        }
        catch (Exception e) {
            exceptionLog.log(e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @DeleteMapping("/id/del")
    public ResponseEntity<Teacher> DeleteTeacher(@RequestParam String id) {
        //TODO: process DELETE request
        try{
            DocumentSnapshot snapshot = repository.getDocumentById(Teacher.class, id);
            if (snapshot == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Teacher teacher = snapshot.toObject(Teacher.class);
            for (int i = 0; i < teacher.getCourseID().size(); i++){
                String getid = teacher.getCourseID().get(i);
                courseController.deleteTeacherinCourse(id, getid);
            }
            repository.deleteDocumentById(Teacher.class, id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            exceptionLog.log(e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/del-all")
    public ResponseEntity<List<Teacher>> deleteAll() {
        try{
            List<Teacher> list = new ArrayList<Teacher>();
            List<DocumentSnapshot> documents = repository.getAllDocuments(Teacher.class);
            if (documents == null || documents.size() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            for (DocumentSnapshot document : documents) {
                Teacher teacher = document.toObject(Teacher.class);
                list.add(teacher);
                
                for (int i = 0; i < teacher.getCourseID().size(); i++){
                    String getid = teacher.getCourseID().get(i);
                    courseController.deleteTeacherinCourse(teacher.getId(), getid);
                }
                
                repository.deleteDocumentById(Teacher.class, document.getId());
            }
            return ResponseEntity.ok().body(list);
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

    @PostMapping("/add/id")
    public ResponseEntity<Teacher> createTeacher(@RequestParam String id, 
                                        @RequestParam (required = false, defaultValue = "") String name, 
                                        @RequestParam String email,
                                        @RequestParam (required = false, defaultValue = "0355916621") String phonenumber,
                                        @RequestParam (required = false, defaultValue = "0000") Integer year,
                                        @RequestParam (required = false, defaultValue =  "01") Integer month,
                                        @RequestParam (required = false, defaultValue = "01") Integer day,
                                        @RequestParam (required = false, defaultValue = "00") Integer hour,
                                        @RequestParam (required = false, defaultValue = "00") Integer minute,
                                        @RequestParam (required = false, defaultValue = "00") Integer second,
                                        @RequestParam (required = false, defaultValue = "null") String master,
                                        @RequestParam (required = false, defaultValue = "null") String phd,
                                        @RequestParam (required = false, defaultValue = "null") String university,
                                        @RequestParam (required = false, defaultValue = "null") String falcuty
                                        ) {
        try{
            
            DocumentSnapshot snapshot = repository.getDocumentById(Teacher.class, id);
            if (snapshot != null) 
            {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            Teacher teacher = new Teacher();
            teacher.setId(id);
            teacher.setName(name);
            teacher.setEmail(email);
            Timestamp value = convertTimestamp(year, month, day, hour, minute, second);
            teacher.setDayofBirth(value);
            teacher.setCourseID(new ArrayList<String>());
            teacher.setPhoneNumber(phonenumber);
            teacher.setCertificate(new Certificate(master, phd, university));
            teacher.setFalcuty(falcuty);
            repository.saveDocument(teacher, id);
            return ResponseEntity.ok().body(teacher);

        }catch (Exception e){
            exceptionLog.log(e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
}
