package com.project.backend.exceptionhandler;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
public class ExceptionLog {
    private final String logFile = "log.txt";
    public void log(Exception exception, String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            writer.printf("%s: %s: %s\n", LocalDateTime.now(), exception, message);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    public void log(Exception exception) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            writer.printf("%s: %s\n", LocalDateTime.now(), exception);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
