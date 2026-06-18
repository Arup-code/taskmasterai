package com.dexcode.taskmasterai.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class SMTPUtil {

    //SMTP module for handling SMTP operations. For now mocks url sending.

    public void sendEmail(String to, String subject, String body) {
        log.info("Sending email to {} with subject {} and body {}", to, subject, body);
    }
}
