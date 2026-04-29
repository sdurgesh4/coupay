package com.trial.dvoc.controller;

import com.trial.dvoc.model.User;
import com.trial.dvoc.repository.UserRepository;
import com.trial.dvoc.service.EmailService;

import jakarta.servlet.http.HttpSession;
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
            HttpSession session,
            Model model
    ){

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
                        100000 +
                                new Random().nextInt(900000)
                );

        session.setAttribute(
                "otp",
                otp
        );

        session.setAttribute(
                "email",
                email
        );

        try{
            emailService.sendOtp(
                    email,
                    otp
            );
        }catch(Exception e){

            e.printStackTrace();

            model.addAttribute(
                    "error",
                    "Mail service unavailable"
            );

            return "forgot_password";
        }

        return "verify_otp";
    }


    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String email,
            @RequestParam String otp,
            Model model){

        User user =
                userRepo.findByEmail(email);

        if(user==null){
            model.addAttribute("error","User not found");
            return "verify_otp";
        }

        String entered =
                otp.trim();

        String saved =
                user.getResetOtp()==null ?
                        "" :
                        user.getResetOtp().trim();

        System.out.println(
                "Entered:"+otp
        );

        System.out.println(
                "Saved:"+user.getResetOtp()
        );

        if(!entered.equals(saved)){
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

        if(
                user.getOtpExpiry()
                        .isBefore(
                                java.time.LocalDateTime.now()
                        )
        ){
            model.addAttribute(
                    "error",
                    "OTP expired"
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