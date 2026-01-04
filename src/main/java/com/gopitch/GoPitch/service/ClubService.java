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

    private ClubResponseDTO convertToClubResponseDTO(Club club) {
        if (club == null)
            return null;

        ClubResponseDTO response = new ClubResponseDTO();
        // ... (Giữ nguyên các trường cũ) ...
        response.setId(club.getId());
        response.setName(club.getName());
        response.setDescription(club.getDescription());
        response.setAddress(club.getAddress());
        response.setPhoneNumber(club.getPhoneNumber());
        response.setImageAvatar(club.getImageAvatar());
        response.setActive(club.isActive());
        response.setTimeStart(club.getTimeStart());
        response.setTimeEnd(club.getTimeEnd());

        // 1. Map Pitch Prices
        if (club.getPitchPrices() != null) {
            response.setPitchPrices(club.getPitchPrices().stream().map(pp -> {
                ClubResponseDTO.PitchPriceDTO dto = new ClubResponseDTO.PitchPriceDTO();
                dto.setName(pp.getName());
                dto.setPrice(pp.getPrice());
                dto.setTimeStart(pp.getTimeStart());
                dto.setTimeEnd(pp.getTimeEnd());
                return dto;
            }).collect(Collectors.toList()));
        }

        // 2. Map Image Clubs
        if (club.getImageClubs() != null) {
            response.setImageClubs(club.getImageClubs().stream().map(img -> {
                ClubResponseDTO.ImageClubDTO dto = new ClubResponseDTO.ImageClubDTO();
                dto.setImageUrl(img.getImageUrl());
                return dto;
            }).collect(Collectors.toList()));
        }

        // 3. Map Comments
        if (club.getComments() != null) {
            response.setComments(club.getComments().stream().map(cmt -> {
                ClubResponseDTO.CommentDTO dto = new ClubResponseDTO.CommentDTO();
                dto.setId(cmt.getId());
                dto.setContent(cmt.getContent());
                dto.setRate(cmt.getRate());
                dto.setUserName(cmt.getUser() != null ? cmt.getUser().getName() : "Ẩn danh");
                return dto;
            }).collect(Collectors.toList()));
        }

        // 4. Map Pitches (Sân con) - THÊM ĐOẠN NÀY VÀO
        if (club.getPitches() != null) {
            response.setPitches(club.getPitches().stream().map(p -> {
                ClubResponseDTO.PitchDTO dto = new ClubResponseDTO.PitchDTO();
                dto.setId(p.getId());
                dto.setName(p.getName());
                dto.setActive(p.isActive());
                return dto;
            }).collect(Collectors.toList()));
        }

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
        clubDB.setActive(requestDTO.isActive());
        clubDB.setTimeStart(requestDTO.getTimeStart());
        clubDB.setTimeEnd(requestDTO.getTimeEnd());

        Club updatedClub = clubRepository.save(clubDB);
        return convertToClubResponseDTO(updatedClub);
    }

    /** Lấy club theo id */
    @Transactional(readOnly = true)
    public ClubResponseDTO getClubById(long id) throws ResourceNotFoundException { // Đổi fetch thành get
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + id));
        return convertToClubResponseDTO(club);
    }

    /** Xóa club */
    @Transactional
    public void deleteClub(long id) throws ResourceNotFoundException {
        Club clubToDelete = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + id));
        clubRepository.delete(clubToDelete);
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

    public ResultPaginationDTO<ClubResponseDTO> searchClubs(String keyword, Pageable pageable) {
        // 1. Lấy dữ liệu từ Repo
        Page<Club> pageClubs = this.clubRepository.findByNameContainingIgnoreCase(keyword, pageable);

        List<ClubResponseDTO> listDTO = pageClubs.getContent()
                .stream()
                .map(this::convertToClubResponseDTO)
                .collect(Collectors.toList());

        ResultPaginationDTO<ClubResponseDTO> rs = new ResultPaginationDTO<>();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageClubs.getTotalPages());
        mt.setTotal(pageClubs.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(listDTO);

        return rs;
    }
}
