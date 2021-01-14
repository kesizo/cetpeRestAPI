package com.kesizo.cetpe.backend.restapi.email.service;

import com.kesizo.cetpe.backend.restapi.email.model.EmailBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService{

	private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

	@Autowired
	private JavaMailSender sender;

	@Value("${spring.mail.username}")
	private String emailFrom;

	@Override
	public boolean sendEmail(EmailBody emailBody)  {
		return sendEmailTool(emailBody.getContent(),emailBody.getEmail(), emailBody.getSubject());
	}

	private boolean sendEmailTool(String textMessage, String email,String subject) {
		boolean send = false;
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);		
		try {
			helper.setFrom(emailFrom);
			helper.setTo(email);
			helper.setText(textMessage, true);
			helper.setSubject(subject);
			sender.send(message);
			send = true;
			logger.info("Email sent to {}",email);
		} catch (MessagingException e) {
			logger.error("Error sending email to {}", email);
			logger.error("Reason email failure {}", e);
		}
		return send;
	}	
}