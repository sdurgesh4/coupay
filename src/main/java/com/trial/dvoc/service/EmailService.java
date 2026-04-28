package com.trial.dvoc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(
            String to,
            String otp){

        SimpleMailMessage mail=
                new SimpleMailMessage();

        mail.setTo(to);

        mail.setSubject(
                "Coupay Password Reset OTP"
        );

        mail.setText(
                "Your OTP is: "+otp+
                        "\nValid for 10 minutes."
        );

        mailSender.send(mail);

    }

}