package com.lms.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service
public class EmailSenderService {

@Autowired
private JavaMailSender mailSender;

	
	public void sendEmail(String to, String subject, String text) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("");
        msg.setSubject("");
        msg.setText("");

        mailSender.send(msg);

    }






    
}
