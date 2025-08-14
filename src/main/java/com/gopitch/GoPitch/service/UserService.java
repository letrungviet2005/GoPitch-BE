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
import com.gopitch.GoPitch.domain.request.auth.RegisterRequestDTO;
import com.gopitch.GoPitch.domain.response.ResultPaginationDTO;
import com.gopitch.GoPitch.domain.response.auth.ResCreateUserDTO;
import com.gopitch.GoPitch.domain.response.user.UserResponseDTO;
import com.gopitch.GoPitch.repository.UserRepository;
import com.gopitch.GoPitch.util.error.DuplicateResourceException;
import com.gopitch.GoPitch.util.error.ResourceNotFoundException;
import com.gopitch.GoPitch.repository.RoleRepository;
import com.gopitch.GoPitch.domain.Role;
import com.gopitch.GoPitch.domain.request.user.CreateUserRequestDTO;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    private UserResponseDTO convertToUserResponseDTO(User user) {
        if (user == null)
            return null;
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setActive(user.isActive());
        dto.setPoint(user.getPoint());
        dto.setStreakCount(user.getStreakCount());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setCreatedBy(user.getCreatedBy());
        dto.setUpdatedBy(user.getUpdatedBy());

        if (user.getRole() != null) {
            UserResponseDTO.RoleInfoDTO roleInfo = new UserResponseDTO.RoleInfoDTO();
            roleInfo.setId(user.getRole().getId());
            roleInfo.setName(user.getRole().getName());
            dto.setRole(roleInfo);
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public User handleGetUsername(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public void updateUserToken(String refreshToken, String email) throws ResourceNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + email + " for updating token."));
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null && user.getRefreshToken() != null && user.getRefreshToken().equals(refreshToken)) {
            return user;
        }
        return null;
    }

    @Transactional
    public UserResponseDTO registerNewUser(RegisterRequestDTO registerDTO)
            throws DuplicateResourceException, ResourceNotFoundException {
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new DuplicateResourceException("Email '" + registerDTO.getEmail() + "' already exists.");
        }

        User newUser = new User();
        newUser.setName(registerDTO.getName());
        newUser.setEmail(registerDTO.getEmail());

        newUser.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        newUser.setActive(true);
        newUser.setPoint(0);

        Long roleIdToAssign = registerDTO.getRoleId();
        Role assignedRole;

        if (roleIdToAssign == null) {
            assignedRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Default role 'USER' not found. Please ensure it exists in the database."));
        } else {

            assignedRole = roleRepository.findById(roleIdToAssign)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleIdToAssign));
        }
        newUser.setRole(assignedRole);

        User savedUser = userRepository.save(newUser);

        return convertToUserResponseDTO(savedUser);
    }

    // Khởi tạo user mới

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities);
    }

    @Transactional
    public UserResponseDTO createUser(CreateUserRequestDTO requestDTO)
            throws DuplicateResourceException, ResourceNotFoundException {
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Email '" + requestDTO.getEmail() + "already exists.");
        }

        User user = new User();
        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setActive(requestDTO.isActive());
        user.setPoint(requestDTO.getPoint());

        Role role = roleRepository.findById(requestDTO.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " +
                        requestDTO.getRoleId()));
        user.setRole(role);

        User savedUser = userRepository.save(user);

        return convertToUserResponseDTO(savedUser);
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
