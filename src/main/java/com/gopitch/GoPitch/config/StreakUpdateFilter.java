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
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Optional<String> userEmailOpt = SecurityUtil.getCurrentUserLogin();

            if (userEmailOpt.isPresent()) {
                userRepository.findByEmail(userEmailOpt.get())
                        .ifPresent(user -> {
                            try {
                                streakService.updateUserStreak(user);
                            } catch (Exception ex) {
                                logger.warn("Streak update failed for user " + user.getEmail(), ex);
                            }
                        });
            }
        } catch (Exception e) {
            logger.error("Could not update user streak", e);
        }

        filterChain.doFilter(request, response);
    }
}
