package com.trial.dvoc.controller;

import com.trial.dvoc.model.User;
import com.trial.dvoc.repository.UserRepository;
import com.trial.dvoc.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Random;

@Controller
public class ForgotPasswordController {

    @Autowired
    UserRepository userRepo;

    @Autowired
    EmailService emailService;



    @GetMapping("/forgot-password")
    public String forgotPage(){
        return "forgot_password";
    }



    @PostMapping("/send-otp")
    public String sendOtp(
            @RequestParam String email,
            Model model){

        User user=
                userRepo.findByEmail(email);

        if(user==null){
            model.addAttribute(
                    "error",
                    "Email not found"
            );
            return "forgot_password";
        }

        String otp=
                String.valueOf(
                        100000+
                                new Random()
                                        .nextInt(900000)
                );

        user.setResetOtp(
                otp
        );

        user.setOtpExpiry(
                LocalDateTime.now()
                        .plusMinutes(10)
        );

        userRepo.save(user);

        emailService.sendOtp(
                email,
                otp
        );

        model.addAttribute(
                "email",
                email
        );

        return "verify_otp";
    }




    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String email,
            @RequestParam String otp,
            Model model){

        User user=
                userRepo.findByEmail(email);

        if(
                user==null ||
                        !otp.equals(
                                user.getResetOtp()
                        ) ||
                        user.getOtpExpiry()
                                .isBefore(
                                        LocalDateTime.now()
                                )
        ){

            model.addAttribute(
                    "error",
                    "Invalid OTP"
            );

            model.addAttribute(
                    "email",
                    email
            );

            return "verify_otp";
        }

        model.addAttribute(
                "email",
                email
        );

        return "reset_password";
    }




    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String email,
            @RequestParam String password){

        User user=
                userRepo.findByEmail(email);

        user.setPassword(
                password
        );

        user.setResetOtp(null);

        userRepo.save(user);

        return "redirect:/login";
    }

}