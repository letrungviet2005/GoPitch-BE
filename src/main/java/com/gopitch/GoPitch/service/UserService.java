package com.gopitch.GoPitch.service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gopitch.GoPitch.domain.User;
import com.gopitch.GoPitch.domain.response.ResultPaginationDTO;
import com.gopitch.GoPitch.domain.response.auth.ResCreateUserDTO;
import com.gopitch.GoPitch.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    public ResCreateUserDTO createUser(ReqCreateUserDTO reqCreateUserDTO) {
        User user = new User();
        user.setEmail(reqCreateUserDTO.getEmail());
        user.setName(reqCreateUserDTO.getName());
        user.setPassword(passwordEncoder.encode(reqCreateUserDTO.getPassword()));
        user.setCreatedAt(Instant.now());

        User savedUser = userRepository.save(user);

        ResCreateUserDTO response = new ResCreateUserDTO();
        response.setId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setName(savedUser.getName());
        response.setCreatedAt(savedUser.getCreatedAt());

        return response;
    }

    public ResultPaginationDTO<User> getUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta(
                page.getNumber() + 1, // page starts from 0
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements());

        return new ResultPaginationDTO<>(meta, page.getContent());
    }
}
