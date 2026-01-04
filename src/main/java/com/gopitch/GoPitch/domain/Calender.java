package com.gopitch.GoPitch.domain;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "calendars")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Calender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private boolean active = true;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "price")
    private double price;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pitch_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "calendars" })
    private Pitch pitch;

    @OneToMany(mappedBy = "calendar", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties("calendar")
    private List<Placed> placeds;
}