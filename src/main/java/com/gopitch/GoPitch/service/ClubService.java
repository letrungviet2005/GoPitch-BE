package com.gopitch.GoPitch.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gopitch.GoPitch.domain.Club;
import com.gopitch.GoPitch.domain.request.club.ClubRequestDTO;
import com.gopitch.GoPitch.domain.response.ResultPaginationDTO;
import com.gopitch.GoPitch.domain.response.club.ClubResponseDTO;
import com.gopitch.GoPitch.repository.ClubRepository;
import com.gopitch.GoPitch.util.error.BadRequestException;
import com.gopitch.GoPitch.util.error.DuplicateResourceException;
import com.gopitch.GoPitch.util.error.ResourceNotFoundException;

@Service
public class ClubService {
    private final ClubRepository clubRepository;
    private final StreakService streakService;

    public ClubService(ClubRepository clubRepository, StreakService streakService) {
        this.clubRepository = clubRepository;
        this.streakService = streakService;
    }

    /** Chuyển entity sang DTO */
    private ClubResponseDTO convertToClubResponseDTO(Club club) {
        if (club == null)
            return null;

        ClubResponseDTO response = new ClubResponseDTO();
        response.setId(club.getId());
        response.setName(club.getName());
        response.setDescription(club.getDescription());
        response.setAddress(club.getAddress());
        response.setPhoneNumber(club.getPhoneNumber());
        response.setImageUrl(club.getImageUrl());
        response.setImageUrl2(club.getImageUrl2());
        response.setImageUrl3(club.getImageUrl3());
        response.setImageUrl4(club.getImageUrl4());
        response.setImageUrl5(club.getImageUrl5());
        response.setImageAvatar(club.getImageAvatar());
        response.setActive(club.isActive());
        response.setTimeStart(club.getTimeStart());
        response.setTimeEnd(club.getTimeEnd());

        // Cập nhật streak cho user nếu có
        if (club.getUser() != null) {
            streakService.updateUserStreak(club.getUser());
        }

        return response;
    }

    /** Tạo mới club */
    @Transactional
    public ClubResponseDTO createClub(ClubRequestDTO requestDTO)
            throws DuplicateResourceException, BadRequestException {

        if (clubRepository.existsByName(requestDTO.getName())) {
            throw new DuplicateResourceException("Club with the same name already exists.");
        }

        Club club = new Club();
        club.setName(requestDTO.getName());
        club.setDescription(requestDTO.getDescription());
        club.setAddress(requestDTO.getAddress());
        club.setPhoneNumber(requestDTO.getPhoneNumber());
        club.setImageAvatar(requestDTO.getImageAvatar()); // Changed to imageAvatar for consistency
        club.setImageUrl(requestDTO.getImageUrl());
        club.setImageUrl2(requestDTO.getImageUrl2());
        club.setImageUrl3(requestDTO.getImageUrl3());
        club.setImageUrl4(requestDTO.getImageUrl4());
        club.setImageUrl5(requestDTO.getImageUrl5());
        club.setActive(requestDTO.isActive());
        club.setTimeStart(requestDTO.getTimeStart());
        club.setTimeEnd(requestDTO.getTimeEnd());

        Club savedClub = clubRepository.save(club);
        return convertToClubResponseDTO(savedClub);
    }

    /** Cập nhật club */
    @Transactional
    public ClubResponseDTO updateClub(long id, ClubRequestDTO requestDTO)
            throws ResourceNotFoundException, DuplicateResourceException, BadRequestException {

        Club clubDB = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + id));

        if (clubRepository.existsByNameAndIdNot(requestDTO.getName(), id)) {
            throw new DuplicateResourceException("Club with the same name already exists.");
        }

        clubDB.setName(requestDTO.getName());
        clubDB.setDescription(requestDTO.getDescription());
        clubDB.setAddress(requestDTO.getAddress());
        clubDB.setPhoneNumber(requestDTO.getPhoneNumber());
        clubDB.setImageAvatar(requestDTO.getImageAvatar()); // Changed to imageAvatar for consistency
        clubDB.setImageUrl(requestDTO.getImageUrl());
        clubDB.setActive(requestDTO.isActive());
        clubDB.setTimeStart(requestDTO.getTimeStart());
        clubDB.setTimeEnd(requestDTO.getTimeEnd());

        Club updatedClub = clubRepository.save(clubDB);
        return convertToClubResponseDTO(updatedClub);
    }

    /** Xóa club */
    @Transactional
    public void deleteClub(long id) throws ResourceNotFoundException {
        Club clubToDelete = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + id));
        clubRepository.delete(clubToDelete);
    }

    /** Lấy club theo id */
    @Transactional(readOnly = true)
    public ClubResponseDTO fetchClubById(long id) throws ResourceNotFoundException {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + id));
        return convertToClubResponseDTO(club);
    }

    /** Lấy danh sách club có phân trang + lọc */
    @Transactional(readOnly = true)
    public ResultPaginationDTO<ClubResponseDTO> fetchAllClubs(Pageable pageable, String name, Boolean active) {
        Specification<Club> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%"));
            }
            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Club> pageClub = clubRepository.findAll(spec, pageable);
        List<ClubResponseDTO> clubDTOs = pageClub.getContent().stream()
                .map(this::convertToClubResponseDTO)
                .collect(Collectors.toList());

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta(
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                pageClub.getTotalPages(),
                pageClub.getTotalElements());

        return new ResultPaginationDTO<>(meta, clubDTOs);
    }
}
