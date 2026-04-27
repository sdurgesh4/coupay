package com.trial.dvoc.service;

import com.trial.dvoc.model.Coupon;
import com.trial.dvoc.model.User;
import com.trial.dvoc.model.Vote;
import com.trial.dvoc.repository.CouponRepository;
import com.trial.dvoc.repository.VoteRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
public class CouponService {

    private final CouponRepository repo;

    private final VoteRepository voteRepo;

    public CouponService(CouponRepository repo, VoteRepository voteRepo) {
        this.repo = repo;
        this.voteRepo = voteRepo;
    }

    public List<Coupon> getAllCoupons() {
        LocalDate today = LocalDate.now();
        return repo.findAll()
                .stream()
                .filter(c -> !c.isUsed())
                .sorted((c1, c2) -> {

                    LocalDate d1 = c1.getExpiryDate();
                    LocalDate d2 = c2.getExpiryDate();

                    // Handle nulls (no expiry → last)
                    if (d1 == null) return 1;
                    if (d2 == null) return -1;

                    boolean e1 = d1.isBefore(today);
                    boolean e2 = d2.isBefore(today);

                    // Active first, expired later
                    if (e1 && !e2) return 1;
                    if (!e1 && e2) return -1;

                    // Same group → sort by date
                    return d1.compareTo(d2);
                })
                .toList();
    }

    public void saveCoupon(Coupon coupon){
        coupon.setQrCodeUrl( getCategoryImage( coupon.getCategory() ));
        repo.save(coupon);
    }

    private String getCategoryImage( String category){

        if(category==null){
            return defaultImage();
        }
        switch( category.toLowerCase() ){

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

            default:
                return defaultImage();
        }
    }

    private String defaultImage(){
        return "https://res.cloudinary.com/dpxhldb4y/image/upload/v1777228405/others_kabsji.jpg";
    }

    public void deleteCoupon(Long id) {
        repo.deleteById(id);
    }

    public void markAsUsed(Long id) {
        Coupon c = repo.findById(id).orElse(null);
        if (c != null) {
            c.setUsed(true);
            repo.save(c);
        }
    }

    public List<Coupon> searchCoupons(String brand) {
        return repo.findByBrandContainingIgnoreCase(brand)
                .stream()
                .filter(c -> !c.isUsed())
                .toList();
    }

    public Coupon buyCoupon(Long couponId, User user) {

        Coupon c=repo.findById(couponId).orElse(null);
        if(c!=null && !c.isUsed()){
            c.setUsed(true);
            c.setUser(user);
            repo.save(c);
        }
        return c;
    }

    public List<Coupon> getUserCoupons(User user) {
        return repo.findAll()
                .stream()
                .filter(c -> c.getUser() != null && c.getUser().getId().equals(user.getId()))
                .toList();
    }
    public Coupon getCouponById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public String getExpiryStatus(Coupon c) {
        if (c.getExpiryDate() == null) return "";
        long days = ChronoUnit.DAYS.between(LocalDate.now(), c.getExpiryDate());

        if (days > 0) {
            return "Expires in " + days + " day" + (days > 1 ? "s" : "");
        } else if (days == 0) {
            return "Expires today";
        } else {
            return "Expired " + Math.abs(days) + " day" + (Math.abs(days) > 1 ? "s" : "") + " ago";
        }
    }

    public List<Coupon> getByCategory(String category) {

        LocalDate today = LocalDate.now();

        return repo.findByCategoryIgnoreCase(category)
                .stream()
                .filter(c -> !c.isUsed())
                .sorted((c1, c2) -> {

                    LocalDate d1 = c1.getExpiryDate();
                    LocalDate d2 = c2.getExpiryDate();

                    if (d1 == null) return 1;
                    if (d2 == null) return -1;

                    boolean e1 = d1.isBefore(today);
                    boolean e2 = d2.isBefore(today);

                    if (e1 && !e2) return 1;
                    if (!e1 && e2) return -1;

                    return d1.compareTo(d2);
                })
                .toList();
    }

    public void vote(
            Long couponId,
            User user,
            boolean isUpvote){

        Coupon coupon=
                repo.findById(couponId)
                        .orElse(null);

        if(coupon==null) return;

        Vote existing=
                voteRepo.findByUserAndCoupon(
                        user,
                        coupon
                ).orElse(null);



        /* FIRST VOTE */
        if(existing==null){

            Vote vote=new Vote();

            vote.setUser(user);
            vote.setCoupon(coupon);
            vote.setUpvote(isUpvote);

            voteRepo.save(vote);

            if(isUpvote){
                coupon.setUpvotes(
                        coupon.getUpvotes()+1
                );
            }else{
                coupon.setDownvotes(
                        coupon.getDownvotes()+1
                );
            }

            repo.save(coupon);
            return;
        }



        /* CLICK SAME AGAIN = REMOVE VOTE */
        if(existing.isUpvote()==isUpvote){

            if(isUpvote){
                coupon.setUpvotes(
                        Math.max(
                                0,
                                coupon.getUpvotes()-1
                        ));
            }else{
                coupon.setDownvotes(
                        Math.max(
                                0,
                                coupon.getDownvotes()-1
                        ));
            }

            voteRepo.delete(existing);

            repo.save(coupon);
            return;
        }



        /* SWITCH VOTE */
        if(isUpvote){

            coupon.setUpvotes(
                    coupon.getUpvotes()+1
            );

            coupon.setDownvotes(
                    Math.max(
                            0,
                            coupon.getDownvotes()-1
                    ));

        }else{

            coupon.setDownvotes(
                    coupon.getDownvotes()+1
            );

            coupon.setUpvotes(
                    Math.max(
                            0,
                            coupon.getUpvotes()-1
                    ));
        }

        existing.setUpvote(
                isUpvote
        );

        voteRepo.save(existing);

        repo.save(coupon);
    }

    public void reportCoupon(Long id) {
        Coupon c = repo.findById(id).orElse(null);
        if (c != null) {
            c.setReported(true);
            repo.save(c);
        }
    }

    public long totalCoupons() {
        return repo.count();
    }

    public long activeCoupons() {
        return repo.findAll().stream().filter(c -> !c.isUsed()).count();
    }

    public long userUsedCoupons(User user) {
        return repo.findAll()
                .stream()
                .filter(c -> c.getUser() != null && c.getUser().getId().equals(user.getId()))
                .count();
    }

    public void unreportCoupon(Long id) {
        Coupon c = repo.findById(id).orElse(null);
        if (c != null) {
            c.setReported(false);
            repo.save(c);
        }
    }

}
