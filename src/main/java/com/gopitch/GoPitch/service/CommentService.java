package com.gopitch.GoPitch.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gopitch.GoPitch.domain.Club;
import com.gopitch.GoPitch.domain.Comment;
import com.gopitch.GoPitch.domain.User;
import com.gopitch.GoPitch.domain.request.comment.CommentRequestDTO;
import com.gopitch.GoPitch.domain.response.comment.CommentResponseDTO;
import com.gopitch.GoPitch.repository.ClubRepository;
import com.gopitch.GoPitch.repository.CommentRepository;
import com.gopitch.GoPitch.repository.UserRepository;
import com.gopitch.GoPitch.util.SecurityUtil;
import com.gopitch.GoPitch.util.error.BadRequestException;
import com.gopitch.GoPitch.util.error.PermissionException;
import com.gopitch.GoPitch.util.error.ResourceNotFoundException;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;

    public CommentService(CommentRepository commentRepository,
            UserRepository userRepository,
            ClubRepository clubRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.clubRepository = clubRepository;
    }

    @Transactional
    public CommentResponseDTO create(CommentRequestDTO request) {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new PermissionException("Chưa đăng nhập"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        Club club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy CLB"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setRate(request.getRate());
        comment.setUser(user);
        comment.setClub(club);
        comment.setCreatedAt(LocalDateTime.now());

        return toDTO(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getByClub(long clubId) {
        if (!clubRepository.existsById(clubId)) {
            throw new ResourceNotFoundException("Không tìm thấy CLB id: " + clubId);
        }
        return commentRepository.findByClubIdOrderByCreatedAtDesc(clubId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(long id) {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new PermissionException("Chưa đăng nhập"));

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bình luận"));

        boolean isOwner = comment.getUser() != null && email.equals(comment.getUser().getEmail());
        User currentUser = userRepository.findByEmail(email).orElse(null);
        boolean isAdmin = currentUser != null && currentUser.getRole() != null
                && "ADMIN".equals(currentUser.getRole().getName());

        if (!isOwner && !isAdmin) {
            throw new PermissionException("Bạn không có quyền xóa bình luận này.");
        }

        commentRepository.delete(comment);
    }

    private CommentResponseDTO toDTO(Comment comment) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setRate(comment.getRate());
        dto.setUserId(comment.getUser() != null ? comment.getUser().getId() : 0);
        dto.setUserName(comment.getUser() != null ? comment.getUser().getName() : "Ẩn danh");
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
