package com.trial.dvoc.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "coupons")
@Data
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String description;
    private String qrCodeUrl;
    private String category;
    private int upvotes = 0;
    private int downvotes = 0;
    private boolean reported = false;
    private boolean used = false;

    @Column(unique = true)
    private String couponCode;
    private String redeemNowUrl;

    private LocalDate expiryDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;   // who purchased it
}
