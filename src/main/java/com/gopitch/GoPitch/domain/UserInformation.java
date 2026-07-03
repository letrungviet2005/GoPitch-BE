package com.gopitch.GoPitch.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_info")
@Getter
@Setter
public class UserInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String fullName;
    private String phoneNumber;
    private String address;

    private Double latitude;
    private Double longitude;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}