package com.project.backend.email;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.project.backend.exceptionhandler.ExceptionLog;
import jakarta.mail.internet.MimeMessage;

/**
 * The EmailService class is responsible for sending emails using a Gmail SMTP
 * server.
 * It configures the JavaMailSenderImpl instance with the necessary properties
 * to send emails,
 * and provides methods to send emails with various parameters, such as the
 * sender, subject,
 * body, recipient(s), and attachments.
 * 
 * The sendEmail methods handle the creation and sending of the email message,
 * and log any
 * exceptions that occur during the process.
 */
@Service
public class EmailService {
    private JavaMailSenderImpl mailSender;
    private final String form = "<div><img src='https://firebasestorage.googleapis.com/v0/b/database-6cded.appspot.com/o/ceobe.png?alt=media&token=e55a1c20-68bb-40d7-be5c-3e49a788eeda' style='width: 40%%; height: auto; border-radius: 10px; margin-left: 30%%'></div><div style='background-color: skyblue; font-family: Arial; padding: 2px 15px; border-radius: 10px;'><h1 style='color: red; font-size: 36px;'>From: %s</h1><p style='font-size: 24px;'>%s</p></div><button style='background-color: darkcyan; border: none; padding: 10px 20px; text-align: center; display: inline-block; font-size: 16px; margin: 4px 2px; cursor: pointer; border-radius: 10px;'><a href='#' style='text-decoration: none; color: white;'>GO TO CLASS</a></button>";
    @Autowired
    private ExceptionLog exceptionLog;

    /**
     * Configures the JavaMailSenderImpl instance with the necessary properties to
     * send emails using a Gmail SMTP server.
     * The host is set to "smtp.gmail.com", the port is set to 587, and the username
     * and password are provided.
     * Additional properties are set to enable SMTP authentication and STARTTLS
     * encryption.
     */
    public EmailService() {
        mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("laughingjack750@gmail.com");
        mailSender.setPassword("qahl pkyt umdq eqzm");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
    }

    /**
     * Sends an email with the specified parameters.
     *
     * @param from    The email address of the sender.
     * @param subject The subject of the email.
     * @param body    The body of the email.
     * @param to      The email address of the recipient.
     * @param files   A list of attachments to include in the email.
     * @return True if the email was sent successfully, false otherwise.
     */
    public boolean sendEmail(String from, String subject, String body, String to,
            List<Map.Entry<String, Resource>> files) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setText(String.format("%s,\n%s", from, body), String.format(form, from, body));
            if (files != null) {
                for (Map.Entry<String, Resource> file : files) {
                    helper.addAttachment(file.getKey(), file.getValue());
                }
            }
            message = helper.getMimeMessage();
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            exceptionLog.log(e, this.getClass().getName());
            return false;
        }
    }

    /**
     * Sends an email with the specified parameters.
     *
     * @param from    The email address of the sender.
     * @param subject The subject of the email.
     * @param body    The body of the email.
     * @param to      A list of email addresses of the recipients.
     * @param files   A list of attachments to include in the email.
     * @return True if the email was sent successfully, false otherwise.
     */
    public boolean sendEmail(String from, String subject, String body, List<String> to,
            List<Map.Entry<String, Resource>> files) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            for (String t : to) {
                helper.addTo(t);
            }
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setText(String.format("%s,\n%s", from, body), String.format(form, from, body));
            if (files != null) {
                for (Map.Entry<String, Resource> file : files) {
                    helper.addAttachment(file.getKey(), file.getValue());
                }
            }
            message = helper.getMimeMessage();
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            exceptionLog.log(e, this.getClass().getName());
            return false;
        }
    }

    /**
     * Sends an email with the specified parameters.
     *
     * @param from    The email address of the sender.
     * @param subject The subject of the email.
     * @param body    The body of the email.
     * @param to      A list of email addresses of the recipients.
     * @return True if the email was sent successfully, false otherwise.
     */
    public boolean sendEmail(String from, String subject, String body, List<String> to) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            for (String t : to) {
                helper.addTo(t);
            }
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setText(String.format("%s,\n%s", from, body), String.format(form, from, body));
            message = helper.getMimeMessage();
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            exceptionLog.log(e, this.getClass().getName());
            return false;
        }
    }

    /**
     * Sends an email with the specified parameters.
     *
     * @param from    The email address of the sender.
     * @param subject The subject of the email.
     * @param body    The body of the email.
     * @param to      The email address of the recipient.
     * @return True if the email was sent successfully, false otherwise.
     */
    public boolean sendEmail(String from, String subject, String body, String to) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.addTo(to);
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setText(String.format("%s,\n%s", from, body), String.format(form, from, body));
            message = helper.getMimeMessage();
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            exceptionLog.log(e, this.getClass().getName());
            return false;
        }
    }
}
