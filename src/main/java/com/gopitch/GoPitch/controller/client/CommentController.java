package com.gopitch.GoPitch.controller.client;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gopitch.GoPitch.domain.request.comment.CommentRequestDTO;
import com.gopitch.GoPitch.domain.response.comment.CommentResponseDTO;
import com.gopitch.GoPitch.service.CommentService;
import com.gopitch.GoPitch.util.annotation.ApiMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @ApiMessage("Create a comment for a club")
    public ResponseEntity<CommentResponseDTO> create(@Valid @RequestBody CommentRequestDTO request) {
        return ResponseEntity.ok(commentService.create(request));
    }

    @GetMapping("/club/{clubId}")
    @ApiMessage("Get comments by club")
    public ResponseEntity<List<CommentResponseDTO>> getByClub(@PathVariable long clubId) {
        return ResponseEntity.ok(commentService.getByClub(clubId));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a comment")
    public ResponseEntity<Map<String, String>> delete(@PathVariable long id) {
        commentService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa bình luận"));
    }
}
