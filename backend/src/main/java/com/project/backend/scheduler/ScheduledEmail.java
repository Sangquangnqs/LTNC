package com.project.backend.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.project.backend.email.EmailService;
import com.project.backend.exceptionhandler.ExceptionLog;

@Service
public class ScheduledEmail {
    @Autowired
    private EmailService sender;
    @Autowired
    private ExceptionLog log;
    @Autowired
    private Firestore firestore;

    /**
     * Scheduled task that sends emails to users who have completed a test with a
     * score of 11.
     * This task runs daily at 11:00 PM UTC and checks for test results in the
     * "testQuiz" Firestore collection
     * that were completed within the last 24-48 hours. It then sends an email to
     * the email addresses associated
     * with those test results.
     */
    @Scheduled(cron = "0 12 23 * * ?", scheduler = "EmailScheduler")
    public void ScheduledEmailSender() {
        // TODO: Implement getting tests mechanism
        CollectionReference collection = firestore.collection("testQuiz");
        Date date_lo = new Date(new Date().getTime() + 86400000);
        Date date_hi = new Date(new Date().getTime() + 172800000);
        Query query = collection.whereGreaterThanOrEqualTo("dl", Timestamp.of(date_lo)).whereLessThan("dl",
                Timestamp.of(date_hi));
        try {
            ApiFuture<QuerySnapshot> snapshots = query.get();
            List<String> to = new ArrayList<>();
            QuerySnapshot snapshot = snapshots.get();
            snapshot.getDocuments().forEach(doc -> {
                if (doc.contains("score") && doc.contains("email") && doc.get("score", int.class) == 11) {
                    to.add(doc.get("email", String.class));
                } else {
                    log.log(new Exception(), doc.getData().toString());
                }
            });
            if (to.isEmpty())
                return;
            sender.sendEmail("TestSheduler", "New Test Scheduler", "Test Scheduler", to);
        } catch (Exception e) {
            log.log(e);
        }
    }
}
