package com.trial.dvoc.service;

import com.trial.dvoc.model.Coupon;
import com.trial.dvoc.model.User;
import com.trial.dvoc.model.Vote;
import com.trial.dvoc.repository.CouponRepository;
import com.trial.dvoc.repository.VoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CouponService {

    private final CouponRepository repo;
    private final VoteRepository voteRepo;

    public CouponService(CouponRepository repo, VoteRepository voteRepo) {
        this.repo = repo;
        this.voteRepo = voteRepo;
    }

    public void saveCoupon(Coupon coupon){

        if(coupon.getCouponCode()==null || coupon.getCouponCode().isBlank()){
            throw new RuntimeException("Coupon code required");
        }

        coupon.setCouponCode(
                coupon.getCouponCode().trim().toUpperCase()
        );

        if(coupon.getCategory()==null || coupon.getCategory().isBlank()){
            coupon.setCategory("other");
        }

        if(coupon.getExpiryDate()==null){
            coupon.setExpiryDate(LocalDate.now().plusDays(7));
        }

        if(coupon.getRedeemNowUrl()==null){
            coupon.setRedeemNowUrl("");
        }

        coupon.setQrCodeUrl(getCategoryImage(coupon.getCategory()));

        try{
            repo.save(coupon);
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    private String getCategoryImage(String category){

        if(category == null){
            return defaultImage();
        }

        switch(category.toLowerCase()){
            case "amazon":
                return "https://res.cloudinary.com/dpxhldb4y/image/upload/v1777228403/amazon_jjcngb.jpg";
            case "flipkart":
                return "https://res.cloudinary.com/dpxhldb4y/image/upload/v1777228404/flipkart_fhdz0v.jpg";
            case "food":
                return "https://res.cloudinary.com/dpxhldb4y/image/upload/v1777228405/food_vhgltm.jpg";
            case "travel":
                return "https://res.cloudinary.com/dpxhldb4y/image/upload/v1777228406/travel_kx4819.jpg";
            case "fashion":
                return "https://res.cloudinary.com/dpxhldb4y/image/upload/v1777228405/fashion_hjrxfz.jpg";
            case "electronics":
                return "https://res.cloudinary.com/dpxhldb4y/image/upload/v1777228404/electronics_banby3.jpg";
            case "cosmetics":
                return "https://res.cloudinary.com/dpxhldb4y/image/upload/v1777228403/cosmetics_ffv8ep.jpg";
            case "other":
                return defaultImage();
            default:
                return defaultImage();
        }
    }

    private String defaultImage(){
        return "https://res.cloudinary.com/dpxhldb4y/image/upload/v1777228405/others_kabsji.jpg";
    }

    @Transactional
    public Coupon buyCoupon(Long couponId, User user){

        Coupon coupon = repo.findById(couponId).orElse(null);

        if(coupon == null) return null;

        if(coupon.isUsed()){
            return coupon;
        }

        coupon.setUsed(true);
        coupon.setUser(user);

        repo.save(coupon);

        return coupon;
    }

    public List<Coupon> getUserCoupons(User user) {
        return repo.findAll().stream()
                .filter(c -> c.getUser()!=null &&
                        c.getUser().getId().equals(user.getId()))
                .toList();
    }

    public Coupon getCouponById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public List<Coupon> getAllCoupons(){
        return repo.findAll().stream()
                .filter(c -> !c.isUsed())
                .toList();
    }
    @Transactional
    public void deleteCoupon(Long id) {

        voteRepo.deleteByCouponId(id);
        repo.deleteById(id);
    }

    public void vote(Long couponId, User user, boolean isUpvote){

        Coupon coupon = repo.findById(couponId).orElse(null);
        if(coupon == null) return;

        Vote existing = voteRepo.findByUserAndCoupon(user, coupon).orElse(null);

        if(existing == null){
            Vote vote = new Vote();
            vote.setUser(user);
            vote.setCoupon(coupon);
            vote.setUpvote(isUpvote);
            voteRepo.save(vote);

            if(isUpvote) coupon.setUpvotes(coupon.getUpvotes()+1);
            else coupon.setDownvotes(coupon.getDownvotes()+1);

        } else {
            if(existing.isUpvote() != isUpvote){

                if(isUpvote){
                    coupon.setUpvotes(coupon.getUpvotes()+1);
                    coupon.setDownvotes(Math.max(0,coupon.getDownvotes()-1));
                } else {
                    coupon.setDownvotes(coupon.getDownvotes()+1);
                    coupon.setUpvotes(Math.max(0,coupon.getUpvotes()-1));
                }

                existing.setUpvote(isUpvote);
                voteRepo.save(existing);
            }
        }

        repo.save(coupon);
    }

    public long totalCoupons(){
        return repo.count();
    }

    public long activeCoupons(){
        return repo.findAll().stream()
                .filter(c -> !c.isUsed())
                .count();
    }

    public List<Coupon> searchCoupons(String keyword){
        return repo.findAll().stream()
                .filter(c ->
                        (c.getBrand()!=null && c.getBrand().toLowerCase().contains(keyword.toLowerCase())) ||
                                (c.getDescription()!=null && c.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                )
                .toList();
    }

    public List<Coupon> getByCategory(String category){
        return repo.findAll().stream()
                .filter(c ->
                        c.getCategory()!=null &&
                                c.getCategory().equalsIgnoreCase(category)
                )
                .toList();
    }

    public void reportCoupon(Long id){
        Coupon coupon = repo.findById(id).orElse(null);
        if(coupon != null){
            coupon.setReported(true);
            repo.save(coupon);
        }
    }

    public void unreportCoupon(Long id){
        Coupon coupon = repo.findById(id).orElse(null);
        if(coupon != null){
            coupon.setReported(false);
            repo.save(coupon);
        }
    }
}