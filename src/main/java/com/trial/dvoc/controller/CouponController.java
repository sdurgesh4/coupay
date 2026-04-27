package com.trial.dvoc.controller;

import com.trial.dvoc.model.Vote;
import com.trial.dvoc.repository.VoteRepository;
import com.trial.dvoc.service.UserService;
import com.trial.dvoc.repository.UserRepository;
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
    private final UserRepository userRepo;
    private final UserService userService;
    private final VoteRepository voteRepo;

    public CouponController(CouponService service, UserService userService, UserRepository userRepo, VoteRepository voteRepo){
        this.service=service;
        this.userService=userService;
        this.userRepo=userRepo;
        this.voteRepo = voteRepo;
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
    public String saveCoupon(@ModelAttribute Coupon coupon, HttpSession session){    
        User user= (User)session.getAttribute("user");        
        if(user==null){
         return "redirect:/login";
        }
        
        service.saveCoupon(coupon);        
        userService.addPoints( user.getId(), 10);
        
        /* refresh session copy */
        User fresh= userRepo.findById( user.getId() ).orElseThrow();
        session.setAttribute( "user", fresh ); 
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
    public String buyCoupon(@PathVariable Long id,
                            HttpSession session) {

        User user=(User)session.getAttribute("user");
        if(user==null) return "redirect:/login";

        Coupon c = service.buyCoupon(id,user);

        if(c.getRedeemNowUrl()!=null &&
                !c.getRedeemNowUrl().isBlank()){
            return "redirect:" + c.getRedeemNowUrl();
        }

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

    @GetMapping("/coupon/{id}")
    public String couponDetails( @PathVariable Long id, Model model,HttpSession session){
        User user= (User)session.getAttribute("user");

        if(user==null){
            return "redirect:/login";
        }
        Coupon coupon=   service.getCouponById(id);
        Vote vote=  voteRepo.findByUserAndCoupon(  user, coupon ).orElse(null);
        if(vote!=null){
            model.addAttribute(
                    "userVote",
                    vote.isUpvote()
                            ? "LIKE"
                            : "DISLIKE"
            );
        }
        model.addAttribute(
                "coupon",
                coupon
        );
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

    //voting
    @GetMapping("/upvote/{id}")
    public String upvote( @PathVariable Long id, HttpSession session){

        User user= (User)session.getAttribute("user");

        if(user==null)
            return "redirect:/login";

        service.vote(id, user,true );
        return "redirect:/coupon/"+id;
    }

    @GetMapping("/downvote/{id}")
    public String downvote( @PathVariable Long id, HttpSession session){

        User user= (User)session.getAttribute("user");

        if(user==null)
            return "redirect:/login";

        service.vote( id, user,false);

        return "redirect:/coupon/"+id;
    }

    // Report
    @GetMapping("/report/{id}")
    public String report(@PathVariable Long id) {
        service.reportCoupon(id);
        return "redirect:/";
    }

    @GetMapping("/share-coupon")
    public String shareCoupon( @RequestParam(required=false) String text, Model model){

        model.addAttribute("sharedText", text );
        return "add_coupon";
    }
}
