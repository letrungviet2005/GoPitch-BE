package com.gopitch.GoPitch.service;
import com.gopitch.GoPitch.domain.Club;
import com.gopitch.GoPitch.domain.UserInformation;
import com.gopitch.GoPitch.repository.ClubRepository;
import com.gopitch.GoPitch.repository.UserInformationRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserInformationService {
    private final UserInformationRepository userInfoRepository;
    private final ClubRepository clubRepository;

    public UserInformationService(UserInformationRepository userInfoRepository, ClubRepository clubRepository) {
        this.userInfoRepository = userInfoRepository;
        this.clubRepository = clubRepository;
    }

    // Công thức Haversine tính khoảng cách giữa 2 tọa độ (KM)
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        return dist * 60 * 1.1515 * 1.609344;
    }

    public List<Club> findNearbyClubs(long userId, double radiusKm) {
        UserInformation info = userInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User info not found"));

        return clubRepository.findAll().stream()
                .filter(club -> calculateDistance(
                        info.getLatitude(), info.getLongitude(),
                        club.getLatitude(), club.getLongitude()) <= radiusKm)
                .collect(Collectors.toList());
    }
}
