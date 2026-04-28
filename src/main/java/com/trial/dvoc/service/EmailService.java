package com.trial.dvoc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

        @Autowired
        JavaMailSender mailSender;

        @Value("${BREVO_FROM}")
        private String fromEmail;

        public void sendOtp(
                String to,
                String otp
        ){

            SimpleMailMessage msg=
                    new SimpleMailMessage();

            msg.setFrom(
                    System.getenv("BREVO_FROM")
            );

            msg.setTo(to);

            msg.setSubject(
                    "Coupay OTP Reset"
            );

            msg.setText(
                    "Your OTP is: "+otp
            );

            mailSender.send(msg);

        }

}
