package com.gopitch.GoPitch.service;

import com.gopitch.GoPitch.domain.Club;
import com.gopitch.GoPitch.domain.ExtraService;
import com.gopitch.GoPitch.domain.request.extraservice.ExtraServiceRequestDTO;
import com.gopitch.GoPitch.domain.response.extraservice.ExtraServiceResponseDTO;
import com.gopitch.GoPitch.repository.ClubRepository;
import com.gopitch.GoPitch.repository.ExtraServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExtraServiceService {
    private final ExtraServiceRepository extraServiceRepository;
    private final ClubRepository clubRepository;

    public ExtraServiceService(ExtraServiceRepository extraServiceRepository, ClubRepository clubRepository) {
        this.extraServiceRepository = extraServiceRepository;
        this.clubRepository = clubRepository;
    }

    // --- LOGIC LẤY DANH SÁCH (Hàm ông đang gọi ở Controller) ---
    public List<ExtraServiceResponseDTO> getServicesByClub(long clubId) {
        List<ExtraService> services = extraServiceRepository.findByClubId(clubId);

        // Chuyển đổi từ Entity sang ResponseDTO bằng Stream
        return services.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    // --- LOGIC TẠO MỚI ---
    @Transactional
    public ExtraServiceResponseDTO createService(ExtraServiceRequestDTO request) {
        Club club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu lạc bộ"));

        ExtraService newService = new ExtraService();
        newService.setName(request.getName());
        newService.setPrice(request.getPrice());
        newService.setUnit(request.getUnit());
        newService.setClub(club);

        ExtraService saved = extraServiceRepository.save(newService);
        return convertToResponseDTO(saved);
    }

    // --- HELPER METHOD: MAPPING ENTITY -> DTO ---
    private ExtraServiceResponseDTO convertToResponseDTO(ExtraService entity) {
        ExtraServiceResponseDTO dto = new ExtraServiceResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        dto.setUnit(entity.getUnit());
        return dto;
    }
}