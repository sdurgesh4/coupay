package com.trial.dvoc.controller;

import com.trial.dvoc.model.Coupon;
import com.trial.dvoc.model.User;
import com.trial.dvoc.service.CouponService;
import com.trial.dvoc.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class AuthController {

    private final UserService service;

    private final CouponService couponService;

    public AuthController(UserService service, CouponService couponService) {
        this.service = service;
        this.couponService = couponService;
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        user.setRole("USER"); // default
        service.register(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {

        User user = service.login(username, password);

        if (user != null) {
            session.setAttribute("user", user);

            if (user.getRole().equals("ADMIN")) {
                return "redirect:/add";
            } else {
                return "redirect:/";
            }
        }

        return "redirect:/login?error";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        model.addAttribute("totalCoupons", couponService.totalCoupons());
        model.addAttribute("activeCoupons", couponService.activeCoupons());
        model.addAttribute("usedCoupons", couponService.userUsedCoupons(user));

        return "profile";
    }

    @PostMapping("/upload-profile")
    public String uploadProfile(@RequestParam("file") MultipartFile file,
                                HttpSession session) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        if (file.isEmpty()) {
            return "redirect:/profile";
        }
        String uploadDir = System.getProperty("user.dir") + "/uploads/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName =
                System.currentTimeMillis() + "_" +
                        file.getOriginalFilename();
        File destination =
                new File(uploadDir + fileName);
        file.transferTo(destination);
        // Save relative URL in DB
        user.setProfileImage("/uploads/" + fileName);
        service.register(user); // updates user

        session.setAttribute("user", user);
        return "redirect:/profile";
    }

}