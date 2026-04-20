package com.trial.dvoc.controller;

import com.trial.dvoc.model.Coupon;
import com.trial.dvoc.model.User;
import com.trial.dvoc.service.CouponService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CouponController {

    private final CouponService service;

    public CouponController(CouponService service) {
        this.service = service;
    }

    // Home
    @GetMapping("/")
    public String viewHome(Model model, HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("coupons", service.getAllCoupons());
        return "index";
    }

    // Add page
    @GetMapping("/add")
    public String showForm(Model model, HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("coupon", new Coupon());
        return "add_coupon";
    }

    // Save
    @PostMapping("/save")
    public String saveCoupon(@ModelAttribute Coupon coupon, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        service.saveCoupon(coupon);
        // reward points
        user.setPoints(user.getPoints() + 10);
        session.setAttribute("user", user);

        return "redirect:/profile";
    }

    // Search
    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {

        List<Coupon> result = service.searchCoupons(keyword);
        model.addAttribute("coupons", result);

        return "index";
    }

    // Buy
    @GetMapping("/buy/{id}")
    public String buyCoupon(@PathVariable Long id, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        service.buyCoupon(id, user);

        return "redirect:/my-coupons";
    }

    // My Coupons
    @GetMapping("/my-coupons")
    public String myCoupons(Model model, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        model.addAttribute("coupons", service.getUserCoupons(user));

        return "my-coupons";
    }

    // Details
    @GetMapping("/coupon/{id}")
    public String couponDetails(@PathVariable Long id, Model model, HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("coupon", service.getCouponById(id));
        return "coupon-details";
    }

    // Category
    @GetMapping("/category/{name}")
    public String filterByCategory(@PathVariable String name, Model model, HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("coupons", service.getByCategory(name));
        return "index";
    }

    // Voting
    @GetMapping("/upvote/{id}")
    public String upvote(@PathVariable Long id, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        service.vote(id, user, true);

        return "redirect:/";
    }

    @GetMapping("/downvote/{id}")
    public String downvote(@PathVariable Long id, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        service.vote(id, user, false);

        return "redirect:/";
    }

    // Report
    @GetMapping("/report/{id}")
    public String report(@PathVariable Long id) {
        service.reportCoupon(id);
        return "redirect:/";
    }
}