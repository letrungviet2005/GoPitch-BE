package com.gopitch.GoPitch.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "extra_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExtraService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name; // Ví dụ: Thuê vợt Yonex
    private double price; // 50000
    private String unit; // lượt/giờ/chai

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;
}
