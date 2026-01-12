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
import com.gopitch.GoPitch.domain.ExtraService;
import com.gopitch.GoPitch.domain.ImageClub;
import com.gopitch.GoPitch.domain.Pitch;
import com.gopitch.GoPitch.domain.PitchPrice;
import com.gopitch.GoPitch.domain.request.club.ClubRequestDTO;
import com.gopitch.GoPitch.domain.response.ResultPaginationDTO;
import com.gopitch.GoPitch.domain.response.club.ClubResponseDTO;
import com.gopitch.GoPitch.repository.ClubRepository;
import com.gopitch.GoPitch.util.error.BadRequestException;
import com.gopitch.GoPitch.util.error.DuplicateResourceException;
import com.gopitch.GoPitch.util.error.ResourceNotFoundException;
import com.gopitch.GoPitch.util.SecurityUtil;
import com.gopitch.GoPitch.domain.User;
import com.gopitch.GoPitch.repository.UserRepository;

@Service
public class ClubService {
    private final ClubRepository clubRepository;
    private final StreakService streakService;
    private final UserRepository userRepository;

    public ClubService(ClubRepository clubRepository, StreakService streakService, UserRepository userRepository) {
        this.clubRepository = clubRepository;
        this.streakService = streakService;
        this.userRepository = userRepository;
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
        response.setLatitude(club.getLatitude());
        response.setLongitude(club.getLongitude());

        // 1. Map Pitch Prices
        if (club.getPitchPrices() != null) {
            response.setPitchPrices(club.getPitchPrices().stream().map(pp -> {
                ClubResponseDTO.PitchPriceDTO dto = new ClubResponseDTO.PitchPriceDTO();
                dto.setId(pp.getId());
                if (pp.getPitch() != null) {
                    dto.setPitchId(pp.getPitch().getId()); // Quan trọng để map với sân con
                }
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
    public ClubResponseDTO createClub(ClubRequestDTO requestDTO) {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new BadRequestException("Chưa đăng nhập"));
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại"));

        Club club = new Club();
        club.setName(requestDTO.getName());
        club.setAddress(requestDTO.getAddress());
        club.setPhoneNumber(requestDTO.getPhoneNumber());
        club.setDescription(requestDTO.getDescription());
        club.setImageAvatar(requestDTO.getImageAvatar());
        club.setTimeStart(requestDTO.getTimeStart());
        club.setTimeEnd(requestDTO.getTimeEnd());
        club.setActive(requestDTO.isActive());
        club.setLatitude(requestDTO.getLatitude());
        club.setLongitude(requestDTO.getLongitude());
        club.setUser(currentUser);

        if (requestDTO.getPitches() != null) {
            List<Pitch> pitches = requestDTO.getPitches().stream().map(pDTO -> {
                Pitch pitch = new Pitch();
                pitch.setName(pDTO.getName());
                pitch.setActive(pDTO.isActive());
                pitch.setClub(club);

                if (pDTO.getPitchPrices() != null) {
                    List<PitchPrice> prices = pDTO.getPitchPrices().stream().map(ppDTO -> {
                        PitchPrice pp = new PitchPrice();
                        pp.setName(ppDTO.getName());
                        pp.setPrice(ppDTO.getPrice());
                        pp.setTimeStart(ppDTO.getTimeStart());
                        pp.setTimeEnd(ppDTO.getTimeEnd());
                        pp.setClub(club);
                        pp.setPitch(pitch);
                        return pp;
                    }).collect(Collectors.toList());
                    pitch.setPitchPrices(prices);
                }
                return pitch;
            }).collect(Collectors.toList());
            club.setPitches(pitches);
        }

        if (requestDTO.getImageClubs() != null) {
            List<ImageClub> imageClubs = requestDTO.getImageClubs().stream().map(imgDTO -> {
                ImageClub imageClub = new ImageClub();
                imageClub.setImageUrl(imgDTO.getImageUrl());
                imageClub.setClub(club); // Link ảnh tới Club
                return imageClub;
            }).collect(Collectors.toList());
            club.setImageClubs(imageClubs);
        }

        if (requestDTO.getExtraServices() != null) {
            List<ExtraService> services = requestDTO.getExtraServices().stream().map(sDTO -> {
                ExtraService service = new ExtraService();
                service.setName(sDTO.getName());
                service.setPrice(sDTO.getPrice());
                service.setClub(club);
                return service;
            }).collect(Collectors.toList());
            club.setExtraServices(services);
        }

        Club savedClub = clubRepository.save(club);
        return convertToClubResponseDTO(savedClub);
    }

    /** Cập nhật club */
    @Transactional
    public ClubResponseDTO updateClub(long id, ClubRequestDTO requestDTO)
            throws ResourceNotFoundException, DuplicateResourceException, BadRequestException {

        Club clubDB = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + id));

        clubDB.setName(requestDTO.getName());
        clubDB.setDescription(requestDTO.getDescription());
        clubDB.setAddress(requestDTO.getAddress());
        clubDB.setPhoneNumber(requestDTO.getPhoneNumber());
        clubDB.setImageAvatar(requestDTO.getImageAvatar());
        clubDB.setActive(requestDTO.isActive());
        clubDB.setTimeStart(requestDTO.getTimeStart());
        clubDB.setTimeEnd(requestDTO.getTimeEnd());
        clubDB.setLatitude(requestDTO.getLatitude());
        clubDB.setLongitude(requestDTO.getLongitude());

        clubDB.getImageClubs().clear();
        clubDB.getExtraServices().clear();
        clubDB.getPitchPrices().clear();
        clubDB.getPitches().clear();

        clubRepository.saveAndFlush(clubDB);

        if (requestDTO.getImageClubs() != null) {
            requestDTO.getImageClubs().stream()
                    .filter(imgDTO -> imgDTO.getImageUrl() != null && !imgDTO.getImageUrl().trim().isEmpty())
                    .forEach(imgDTO -> {
                        ImageClub img = new ImageClub();
                        img.setImageUrl(imgDTO.getImageUrl());
                        img.setClub(clubDB);
                        clubDB.getImageClubs().add(img);
                    });
        }

        if (requestDTO.getExtraServices() != null) {
            requestDTO.getExtraServices().forEach(sDTO -> {
                ExtraService service = new ExtraService();
                service.setName(sDTO.getName());
                service.setPrice(sDTO.getPrice());
                service.setClub(clubDB);
                clubDB.getExtraServices().add(service);
            });
        }

        if (requestDTO.getPitches() != null) {
            for (var pDTO : requestDTO.getPitches()) {
                Pitch pitch = new Pitch();
                pitch.setName(pDTO.getName());
                pitch.setActive(pDTO.isActive());
                pitch.setClub(clubDB);

                if (requestDTO.getPitchPrices() != null) {
                    List<PitchPrice> pricesForThisPitch = requestDTO.getPitchPrices().stream()
                            .filter(ppDTO -> String.valueOf(ppDTO.getPitchId()).equals(String.valueOf(pDTO.getId())))
                            .map(ppDTO -> {
                                PitchPrice pp = new PitchPrice();
                                pp.setName(ppDTO.getName());
                                pp.setPrice(ppDTO.getPrice());
                                pp.setTimeStart(ppDTO.getTimeStart());
                                pp.setTimeEnd(ppDTO.getTimeEnd());
                                pp.setClub(clubDB);
                                pp.setPitch(pitch);
                                return pp;
                            }).collect(Collectors.toList());

                    pitch.setPitchPrices(pricesForThisPitch);
                    clubDB.getPitchPrices().addAll(pricesForThisPitch);
                }

                clubDB.getPitches().add(pitch);
            }
        }

        // 8. Lưu và trả về
        Club updatedClub = clubRepository.save(clubDB);
        return convertToClubResponseDTO(updatedClub);
    }

    @Transactional(readOnly = true)
    public ClubResponseDTO getClubById(long id) throws ResourceNotFoundException { // Đổi fetch thành get
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + id));
        return convertToClubResponseDTO(club);
    }

    @Transactional
    public void deleteClub(long id) throws ResourceNotFoundException {
        Club clubToDelete = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + id));
        clubRepository.delete(clubToDelete);
    }

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

    @Transactional(readOnly = true)
    public ResultPaginationDTO<ClubResponseDTO> fetchClubsByOwner(Pageable pageable) {
        String currentUserEmail = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new BadRequestException("Không tìm thấy thông tin đăng nhập"));

        Page<Club> pageClub = clubRepository.findByUserEmail(currentUserEmail, pageable);

        List<ClubResponseDTO> listDTO = pageClub.getContent().stream()
                .map(this::convertToClubResponseDTO)
                .collect(Collectors.toList());

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta(
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                pageClub.getTotalPages(),
                pageClub.getTotalElements());

        return new ResultPaginationDTO<>(meta, listDTO);
    }
}
