package com.kanku.service;

import jakarta.mail.MessagingException;

public interface IMailService {

    String sendMail(String msg,String subject,String toEmail) throws MessagingException;
}
