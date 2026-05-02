package com.trial.dvoc.repository;

import com.trial.dvoc.model.Vote;
import com.trial.dvoc.model.User;
import com.trial.dvoc.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByUserAndCoupon(User user, Coupon coupon);
    @Modifying
    @Query("DELETE FROM Vote v WHERE v.coupon.id = :couponId")
    void deleteByCouponId(Long couponId);
}
