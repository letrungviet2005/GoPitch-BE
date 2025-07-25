package com.gopitch.GoPitch.config;

import java.io.IOException;
import java.util.Optional;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gopitch.GoPitch.domain.User;
import com.gopitch.GoPitch.repository.UserRepository;
import com.gopitch.GoPitch.service.StreakService;
import com.gopitch.GoPitch.util.SecurityUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class StreakUpdateFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final StreakService streakService;

    public StreakUpdateFilter(UserRepository userRepository, @Lazy StreakService streakService) {
        this.userRepository = userRepository;
        this.streakService = streakService;
    }

    @Override
    @Transactional
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            Optional<String> userEmailOpt = SecurityUtil.getCurrentUserLogin();

            if (userEmailOpt.isPresent()) {
                Optional<User> userOpt = userRepository.findByEmail(userEmailOpt.get());
                if (userOpt.isPresent()) {
                    streakService.updateUserStreak(userOpt.get());
                }
            }
        } catch (Exception e) {
            logger.error("Could not update user streak", e);
        }

        filterChain.doFilter(request, response);
    }
}