package com.gopitch.GoPitch.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gopitch.GoPitch.domain.Club;
import com.gopitch.GoPitch.domain.response.club.ClubResponseDTO;
import com.gopitch.GoPitch.repository.ClubRepository;
import com.gopitch.GoPitch.service.StreakService;

@Service
public class ClubService {
    private final ClubRepository clubRepository;
    private final StreakService streakService;

    public ClubService(ClubRepository clubRepository, StreakService streakService) {
        this.clubRepository = clubRepository;
        this.streakService = streakService;
    }

    @Transactional
    public ClubResponseDTO convertTClubResponseDTO(Club club) {
        ClubResponseDTO response = new ClubResponseDTO();
        response.setName(club.getName());
        response.setDescription(club.getDescription());
        response.setAddress(club.getAddress());
        response.setPhoneNumber(club.getPhoneNumber());
        response.setImageUrl(club.getImageUrl());
        response.setActive(club.isActive());

        // Update user streak if applicable
        if (club.getUser() != null) {
            streakService.updateUserStreak(club.getUser());
        }

        return response;
    }

}
