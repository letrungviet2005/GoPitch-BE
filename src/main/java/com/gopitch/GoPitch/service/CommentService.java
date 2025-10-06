package com.gopitch.GoPitch.service;

import org.springframework.stereotype.Service;
import com.gopitch.GoPitch.repository.CommentRepository;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
    }

}
