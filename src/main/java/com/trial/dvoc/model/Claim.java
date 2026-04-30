package com.trial.dvoc.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"user_id","coupon_id"}))
@Data
public class Claim {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Coupon coupon;

    private boolean used = true;
}