package com.trial.dvoc.repository;

import com.trial.dvoc.model.Vote;
import com.trial.dvoc.model.User;
import com.trial.dvoc.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByUserAndCoupon(User user, Coupon coupon);
    void deleteByCouponId(Long couponId);
}