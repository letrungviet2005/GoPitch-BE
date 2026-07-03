package com.gopitch.GoPitch.service;

import org.springframework.stereotype.Service;
import com.gopitch.GoPitch.domain.ImageClub;
import com.gopitch.GoPitch.repository.ImageClubRepository;

@Service
public class ImageClubService {
    private final ClubService clubService;
    private final ImageClubRepository imageClubRepository;

    public ImageClubService(ClubService clubService, ImageClubRepository imageClubRepository) {
        this.clubService = clubService;
        this.imageClubRepository = imageClubRepository;
    }

    private ImageClub convertToImageClub(ImageClub imageClub) {
        if (imageClub == null)
            return null;

        ImageClub response = new ImageClub();
        response.setId(imageClub.getId());
        response.setImageUrl(imageClub.getImageUrl());
        response.setClub(imageClub.getClub());

        return response;
    }

}
