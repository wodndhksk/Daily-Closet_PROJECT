package com.megait.soir.service;

import com.megait.soir.domain.Member;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@AllArgsConstructor
@Slf4j
public class SendEmailService {

    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String body, String topic){
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(to);
            simpleMailMessage.setSubject(topic);
            simpleMailMessage.setText(body);
            javaMailSender.send(simpleMailMessage);
            log.info("send");
            System.out.println("sending email!! ");
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
