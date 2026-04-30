package com.trial.dvoc.repository;

import com.trial.dvoc.model.Claim;
import com.trial.dvoc.model.Coupon;
import com.trial.dvoc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends JpaRepository<Claim,Long> {
    Optional<Claim> findByUserAndCoupon(User user, Coupon coupon);
    List<Claim> findByUser(User user);
}
