package com.kanku.service.impl;

import com.kanku.service.IMailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MailServiceImpl implements IMailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("{spring.mail.username}")
    private String fromEmailId;

    @Override
    public String sendMail(String message,String subject, String toEmail) throws MessagingException {

        MimeMessage mimeMsg = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMsg);
        helper.setFrom(fromEmailId);
        helper.setCc(toEmail);
        helper.setSubject(subject);
        helper.setSentDate(new Date());
        //helper.addAttachment("guru.jpg", new ClassPathResource("guru.jpg"));
        helper.setText(message);

        javaMailSender.send(mimeMsg);

        return "Mail sent! Kindly pliz check...!";
    }
}
