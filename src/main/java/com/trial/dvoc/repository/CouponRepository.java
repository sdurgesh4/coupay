package com.trial.dvoc.repository;
import com.trial.dvoc.model.Coupon;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    List<Coupon> findByBrandContainingIgnoreCase(String brand);
    List<Coupon> findByCategoryIgnoreCase(String category);
}
