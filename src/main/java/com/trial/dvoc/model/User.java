package com.trial.dvoc.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    @Column(unique=true,nullable=false)
    private String email;
    private String password;

    private String profileImage; // URL or path
    
    @Column(nullable=false)
    private Integer points = 0;
    private String role; // ADMIN or USER

    private String resetOtp;
    private LocalDateTime otpExpiry;
}
