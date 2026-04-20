package com.trial.dvoc.controller;

import com.trial.dvoc.service.CouponService;
import com.trial.dvoc.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminController {

    private final CouponService couponService;
    private final UserService userService;

    public AdminController(CouponService couponService, UserService userService) {
        this.couponService = couponService;
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String adminPanel(Model model, HttpSession session) {

        var user = session.getAttribute("user");
        if (user == null || !((com.trial.dvoc.model.User) user).getRole().equals("ADMIN")) {
            return "redirect:/";
        }

        model.addAttribute("coupons", couponService.getAllCoupons());
        model.addAttribute("users", userService.getAllUsers());

        return "admin";
    }

    @GetMapping("/admin/delete-coupon/{id}")
    public String deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/remove-report/{id}")
    public String removeReport(@PathVariable Long id) {
        couponService.unreportCoupon(id);
        return "redirect:/admin";
    }
}