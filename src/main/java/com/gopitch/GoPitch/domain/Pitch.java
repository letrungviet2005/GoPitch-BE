package com.gopitch.GoPitch.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gopitch.GoPitch.util.SecurityUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CascadeType;

@Entity
@Table(name = "pitches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pitch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Role name cannot be blank")
    @Size(max = 100, message = "Role name cannot exceed 100 characters")
    private String name;

    private boolean active = true;

    @Column(name = "created_at")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;

    @Column(name = "updated_at")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    // many pitch to one club
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Club club;

    @OneToMany(mappedBy = "pitch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Calender> calendars;

}
