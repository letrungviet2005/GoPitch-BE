package com.gopitch.GoPitch.controller.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gopitch.GoPitch.domain.request.club.ClubRequestDTO;
import com.gopitch.GoPitch.domain.response.ResultPaginationDTO;
import com.gopitch.GoPitch.domain.response.club.ClubResponseDTO;
import com.gopitch.GoPitch.service.ClubService;
import com.gopitch.GoPitch.util.annotation.ApiMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/owner")
public class OwnerClubController {

    private final ClubService clubService;

    public OwnerClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    // 1. Lấy danh sách sân của riêng tôi (Owner)
    @GetMapping("/clubs")
    @ApiMessage("Fetch clubs by owner")
    public ResponseEntity<ResultPaginationDTO<ClubResponseDTO>> getMyClubs(Pageable pageable) {
        return ResponseEntity.ok(this.clubService.fetchClubsByOwner(pageable));
    }

    // 2. Lấy chi tiết 1 sân để sửa
    @GetMapping("/clubs/{id}")
    @ApiMessage("Get club detail")
    public ResponseEntity<ClubResponseDTO> getClubById(@PathVariable("id") long id) {
        return ResponseEntity.ok(this.clubService.getClubById(id));
    }

    // 3. Tạo mới sân (Dành cho trang ClubAdd)
    @PostMapping("/clubs")
    @ApiMessage("Create new club")
    public ResponseEntity<ClubResponseDTO> createNewClub(@Valid @RequestBody ClubRequestDTO requestDTO) {
        ClubResponseDTO newClub = this.clubService.createClub(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newClub);
    }

    // 4. Cập nhật sân
    @PutMapping("/clubs/{id}")
    @ApiMessage("Update club")
    public ResponseEntity<ClubResponseDTO> updateClub(
            @PathVariable("id") long id,
            @Valid @RequestBody ClubRequestDTO requestDTO) {
        return ResponseEntity.ok(this.clubService.updateClub(id, requestDTO));
    }

    // 5. Xóa sân
    @DeleteMapping("/clubs/{id}")
    @ApiMessage("Delete club")
    public ResponseEntity<Void> deleteClub(@PathVariable("id") long id) {
        this.clubService.deleteClub(id);
        return ResponseEntity.ok(null);
    }
}