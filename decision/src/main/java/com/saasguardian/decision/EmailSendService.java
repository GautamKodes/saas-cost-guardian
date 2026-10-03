package com.saasguardian.decision;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailSendService {
    @Autowired
    private JavaMailSender javaMailSender;
     public void sendEmail(String to, String subject, String body){
         try {
             SimpleMailMessage mail  = new SimpleMailMessage();
             mail.setTo(to);
             mail.setSubject(subject);
             mail.setText(body);
             javaMailSender.send(mail);
             log.info("Mail service used");
         } catch (Exception e){
             log.error("Error sending mail", e);
         }
     }
}
