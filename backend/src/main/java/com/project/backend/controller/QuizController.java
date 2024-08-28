package com.project.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.project.backend.Course.Course;
import com.project.backend.QuizMain.Quizz;
import com.project.backend.QuizMain.QuizzDetail;
import com.project.backend.QuizMain.QA.QuestionAndAnswer;
import com.project.backend.Student.Student;
import com.project.backend.exceptionhandler.ExceptionLog;
import com.project.backend.repository.FirestoreRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private FirestoreRepository repository;
    @Autowired
    private ExceptionLog exceptionLog;
    
    
    @GetMapping("/get-score/id")
    public ResponseEntity<QuizzDetail> getScore(@RequestParam String idStudent, @RequestParam String idCourse, @RequestParam Integer number) {
        try{
            DocumentSnapshot documentSnapshot = repository.getDocumentById(Course.class, idCourse);
            DocumentSnapshot documentSnapshot2 = repository.getDocumentById(Student.class, idStudent);
            if (documentSnapshot == null || documentSnapshot2 == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Course course = documentSnapshot.toObject(Course.class);

            Student student = documentSnapshot2.toObject(Student.class);

            boolean exist = false;
            for (int i = 0 ; i < student.getCourseID().size() ; i++ ) {
                if ( student.getCourseID().get(i).getId().equals(idCourse) )
                    exist = true;
            }
            if (exist == false) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Map<String, QuizzDetail> score = course.getListQuizz().get(number).getInfo();
            QuizzDetail quizzDetail = score.get(idStudent);
            if (quizzDetail == null) {
                // if student didn't complete the quiz
                QuizzDetail zeroScore = new QuizzDetail(0.0, null, false);
                return ResponseEntity.ok().body(zeroScore);
            }
            return ResponseEntity.ok().body(quizzDetail);
            
        } catch (Exception e) {
            exceptionLog.log(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // create quiz for specific course
    @PostMapping("/new")
    public ResponseEntity<Quizz> create(@RequestParam String idCourse,
                                        @RequestParam (required = false, defaultValue = "2024") Integer year,
                                        @RequestParam (required = false, defaultValue =  "01") Integer month,
                                        @RequestParam (required = false, defaultValue = "01") Integer day,
                                        @RequestParam (required = false, defaultValue = "00") Integer hour,
                                        @RequestParam (required = false, defaultValue = "00") Integer minute,
                                        @RequestParam (required = false, defaultValue = "00") Integer second,
                                        @RequestParam (required = false, defaultValue = "07") Integer dayleft,
                                        @RequestParam (required = false, defaultValue = "NULL") String title,
                                        @RequestParam (required = false, defaultValue = "3") Integer times
    ){
        try{
            DocumentSnapshot snapshot = repository.getDocumentById(Course.class, idCourse);
            if (snapshot == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Timestamp start = convertTimestamp(year, month, day, hour, minute, second);
            long secondsleft = dayleft*24*60*60;
            // end date = start date + dayleft
            Timestamp end = Timestamp.ofTimeSecondsAndNanos(start.getSeconds() + secondsleft , start.getNanos());

            List<QuestionAndAnswer> questionAndAnswer = new ArrayList<QuestionAndAnswer>();
            Map<String, QuizzDetail> savedScore = new HashMap<String, QuizzDetail>(); 
            Quizz quiz = new Quizz(start, end, questionAndAnswer, savedScore, title, times );

            Course course = snapshot.toObject(Course.class);
            course.getListQuizz().add(quiz);
            repository.updateDocumentById(course);
            return ResponseEntity.ok().body(quiz);
        }catch(Exception e){
            exceptionLog.log(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @PutMapping("id/score")
    public ResponseEntity<Map<String,QuizzDetail>> adjustScore(@RequestParam String id, 
                                                          @RequestParam String idCourse, 
                                                          @RequestParam Integer number,
                                                          @RequestParam (required = false, defaultValue = "00") Double score
                                                          ) {
        try{
            DocumentSnapshot documentSnapshot = repository.getDocumentById(Course.class, idCourse);
            DocumentSnapshot studentsnapshot = repository.getDocumentById(Student.class, id);
            if (documentSnapshot == null || studentsnapshot == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Student student = studentsnapshot.toObject(Student.class);

            boolean exist = false;
            for (int i = 0 ; i < student.getCourseID().size() ; i++ ) {
                if ( student.getCourseID().get(i).getId().equals(idCourse) )
                    exist = true;
            }
            if (exist == false) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            Course course = documentSnapshot.toObject(Course.class);
            if (course.getListQuizz().size() < number + 1) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            Map<String, QuizzDetail> detail = course.getListQuizz().get(number).getInfo();

            List<String> his = new ArrayList<String>();
            QuizzDetail quizzDetail = new QuizzDetail(score, his, (score>=2)? true:false);
            // course.getListQuizz().;
            detail.put(id, quizzDetail );
            repository.updateDocumentById(course);            
            return ResponseEntity.ok().body(detail);
        } catch (Exception e) {
            exceptionLog.log(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("adjust/question")
    public ResponseEntity<Quizz> question_detail(@RequestParam Integer number, 
                                                @RequestParam String idCourse,
                                                @RequestBody List<QuestionAndAnswer> questionAndAnswer){
        try {
            DocumentSnapshot documentSnapshot = repository.getDocumentById(Course.class,idCourse );
            if (documentSnapshot == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Course course = documentSnapshot.toObject(Course.class);
            
            if (course.getListQuizz().size() < number  + 1) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            course.getListQuizz().get(number).setQuestionAndAnswer(questionAndAnswer);

            repository.updateDocumentById(course);            
            return ResponseEntity.ok().body(course.getListQuizz().get(number));

        } catch (Exception e) {
            exceptionLog.log(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/add/question")
    public ResponseEntity<Quizz> addQuestion(@RequestParam Integer number, 
                                                @RequestParam String idCourse,
                                                @RequestBody List<QuestionAndAnswer> questionAndAnswer){
        try {
            DocumentSnapshot documentSnapshot = repository.getDocumentById(Course.class,idCourse );
            if (documentSnapshot == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Course course = documentSnapshot.toObject(Course.class);
            
            if (course.getListQuizz().size() < number  + 1) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            for (int i = 0 ; i < questionAndAnswer.size() ; i++) 
                course.getListQuizz().get(number).getQuestionAndAnswer().add(questionAndAnswer.get(i));
            
            repository.updateDocumentById(course);            
            return ResponseEntity.ok().body(course.getListQuizz().get(number));

        } catch (Exception e) {
            exceptionLog.log(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    public Timestamp convertTimestamp(Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer second){
        LocalDateTime now = LocalDateTime.of(year, month, day, hour, minute, second);
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        Timestamp timestamp = Timestamp.of(date);
        return timestamp;
    }
}
