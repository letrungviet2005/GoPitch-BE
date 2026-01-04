package com.gopitch.GoPitch.controller.client;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gopitch.GoPitch.domain.response.ResultPaginationDTO;
import com.gopitch.GoPitch.domain.response.club.ClubResponseDTO;
import com.gopitch.GoPitch.service.ClubService;
import com.gopitch.GoPitch.util.annotation.ApiMessage;
import com.gopitch.GoPitch.util.error.ResourceNotFoundException;

@RestController("clientClubController")
@RequestMapping("/api/v1/clubs")
public class ClubController {

    private final ClubService clubService;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    @GetMapping("/{id}")
    @ApiMessage("Fetch a club by its ID")
    public ResponseEntity<ClubResponseDTO> getClubById(@PathVariable long id) throws ResourceNotFoundException {
        ClubResponseDTO club = clubService.getClubById(id);
        if (club == null) {
            throw new ResourceNotFoundException("Club not found with ID: " + id);
        }
        return ResponseEntity.ok(club);
    }

    @GetMapping
    @ApiMessage("Fetch all clubs with pagination and filtering")
    public ResponseEntity<ResultPaginationDTO<ClubResponseDTO>> getAllClubs(
            @PageableDefault(size = 9, page = 0) Pageable pageable, // Thêm mặc định ở đây
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "active", required = false) Boolean active) {
        ResultPaginationDTO<ClubResponseDTO> result = clubService.fetchAllClubs(pageable, name, active);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    @ApiMessage("Search clubs by name")
    public ResponseEntity<ResultPaginationDTO<ClubResponseDTO>> searchClubs(
            @PageableDefault(size = 9, page = 0) Pageable pageable,
            @RequestParam("keyword") String keyword) {

        // Nếu keyword rỗng thì trả về tất cả hoặc danh sách trống tùy ông
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.ok(this.clubService.fetchAllClubs(pageable, null, null));
        }

        ResultPaginationDTO<ClubResponseDTO> result = clubService.searchClubs(keyword, pageable);
        return ResponseEntity.ok(result);
    }
}
