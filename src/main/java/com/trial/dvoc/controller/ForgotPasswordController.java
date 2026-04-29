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
    private UserRepository userRepo;

    @Autowired
    private EmailService emailService;



    @GetMapping("/forgot-password")
    public String forgotPage() {
        return "forgot_password";
    }



    @PostMapping("/send-otp")
    public String sendOtp(
            @RequestParam String email,
            HttpSession session,
            Model model) {

        User user =
                userRepo.findByEmail(email);

        if (user == null) {

            model.addAttribute(
                    "error",
                    "Email not found"
            );

            return "forgot_password";
        }


        String otp =
                String.valueOf(
                        100000 +
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


        session.setAttribute(
                "resetEmail",
                email
        );


        try {

            emailService.sendOtp(
                    email,
                    otp
            );

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute(
                    "error",
                    "Failed to send OTP"
            );

            return "forgot_password";
        }


        return "verify_otp";
    }




    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String otp,
            HttpSession session,
            Model model) {

        String email =
                (String) session.getAttribute(
                        "resetEmail"
                );


        if(email==null){

            model.addAttribute(
                    "error",
                    "Session expired. Start again."
            );

            return "forgot_password";
        }


        User user =
                userRepo.findByEmail(email);


        if(user==null){

            model.addAttribute(
                    "error",
                    "User not found"
            );

            return "verify_otp";
        }


        String entered =
                otp.trim();

        String saved =
                user.getResetOtp()==null
                        ? ""
                        : user.getResetOtp()
                          .trim();


        System.out.println(
                "Entered OTP: "
                        + entered
        );

        System.out.println(
                "Saved OTP: "
                        + saved
        );


        if(
                !entered.equals(saved)
        ){

            model.addAttribute(
                    "error",
                    "Invalid OTP"
            );

            return "verify_otp";
        }



        if(
                user.getOtpExpiry()
                        .isBefore(
                                LocalDateTime.now()
                        )
        ){

            model.addAttribute(
                    "error",
                    "OTP expired"
            );

            return "verify_otp";
        }


        return "reset_password";
    }





    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String password,
            HttpSession session,
            Model model) {

        String email =
                (String) session.getAttribute(
                        "resetEmail"
                );


        if(email==null){

            return "redirect:/forgot-password";
        }


        User user =
                userRepo.findByEmail(email);


        if(user==null){

            return "redirect:/forgot-password";
        }


        user.setPassword(
                password
        );


        user.setResetOtp(null);

        user.setOtpExpiry(null);


        userRepo.save(user);


        session.removeAttribute(
                "resetEmail"
        );


        return "redirect:/login";
    }

}