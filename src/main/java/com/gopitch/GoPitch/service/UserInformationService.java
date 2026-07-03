package com.gopitch.GoPitch.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gopitch.GoPitch.domain.Club;
import com.gopitch.GoPitch.domain.User;
import com.gopitch.GoPitch.domain.UserInformation;
import com.gopitch.GoPitch.domain.request.user.UpdateUserInformationRequestDTO;
import com.gopitch.GoPitch.domain.response.user.UserResponseDTO;
import com.gopitch.GoPitch.repository.ClubRepository;
import com.gopitch.GoPitch.repository.UserInformationRepository;
import com.gopitch.GoPitch.repository.UserRepository;
import com.gopitch.GoPitch.util.SecurityUtil;
import com.gopitch.GoPitch.util.error.ResourceNotFoundException;

@Service
public class UserInformationService {

    private final UserInformationRepository userInfoRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;

    public UserInformationService(UserInformationRepository userInfoRepository,
            ClubRepository clubRepository,
            UserRepository userRepository) {
        this.userInfoRepository = userInfoRepository;
        this.clubRepository = clubRepository;
        this.userRepository = userRepository;
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        return dist * 60 * 1.1515 * 1.609344;
    }

    @Transactional(readOnly = true)
    public List<Club> findNearbyClubs(long userId, double radiusKm) {
        UserInformation info = userInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin người dùng"));

        if (info.getLatitude() == null || info.getLongitude() == null) {
            throw new ResourceNotFoundException("Người dùng chưa cập nhật tọa độ.");
        }

        return clubRepository.findNearestClubs(info.getLatitude(), info.getLongitude(), radiusKm);
    }

    @Transactional
    public UserResponseDTO updateMyProfile(UpdateUserInformationRequestDTO request) {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new ResourceNotFoundException("Chưa đăng nhập"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        UserInformation info = user.getUserInformation();
        if (info == null) {
            info = new UserInformation();
            info.setUser(user);
            user.setUserInformation(info);
        }

        if (request.getFullName() != null) {
            info.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            info.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            info.setAddress(request.getAddress());
        }
        if (request.getLatitude() != null) {
            info.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            info.setLongitude(request.getLongitude());
        }

        userInfoRepository.save(info);

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPoint(user.getPoint());
        dto.setStreakCount(user.getStreakCount());

        UserResponseDTO.UserInfoDTO infoDTO = new UserResponseDTO.UserInfoDTO();
        infoDTO.setFullName(info.getFullName());
        infoDTO.setPhoneNumber(info.getPhoneNumber());
        infoDTO.setAddress(info.getAddress());
        infoDTO.setLatitude(info.getLatitude());
        infoDTO.setLongitude(info.getLongitude());
        dto.setUserInformation(infoDTO);

        return dto;
    }
}
