package com.gopitch.GoPitch.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gopitch.GoPitch.domain.Permission;
import com.gopitch.GoPitch.domain.Role;
import com.gopitch.GoPitch.domain.request.role.RoleRequestDTO;
import com.gopitch.GoPitch.domain.response.ResultPaginationDTO;
import com.gopitch.GoPitch.domain.response.role.RoleResponseDTO;
import com.gopitch.GoPitch.repository.PermissionRepository;
import com.gopitch.GoPitch.repository.RoleRepository;
import com.gopitch.GoPitch.util.error.DuplicateResourceException;
import com.gopitch.GoPitch.util.error.ResourceNotFoundException;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Transactional(readOnly = true)
    public ResultPaginationDTO<RoleResponseDTO> fetchAll(Pageable pageable) {
        Page<Role> pageRole = roleRepository.findAll(pageable);
        List<RoleResponseDTO> roles = pageRole.getContent().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta(
                pageRole.getNumber() + 1,
                pageRole.getSize(),
                pageRole.getTotalPages(),
                pageRole.getTotalElements());

        return new ResultPaginationDTO<>(meta, roles);
    }

    @Transactional(readOnly = true)
    public RoleResponseDTO fetchById(long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return toResponseDTO(role);
    }

    @Transactional
    public RoleResponseDTO create(RoleRequestDTO requestDTO) {
        if (roleRepository.existsByName(requestDTO.getName())) {
            throw new DuplicateResourceException("Role already exists with name: " + requestDTO.getName());
        }

        Role role = new Role();
        applyRequest(role, requestDTO);
        return toResponseDTO(roleRepository.save(role));
    }

    @Transactional
    public RoleResponseDTO update(long id, RoleRequestDTO requestDTO) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        if (roleRepository.existsByNameAndIdNot(requestDTO.getName(), id)) {
            throw new DuplicateResourceException("Role already exists with name: " + requestDTO.getName());
        }

        applyRequest(role, requestDTO);
        return toResponseDTO(roleRepository.save(role));
    }

    @Transactional
    public void delete(long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        roleRepository.delete(role);
    }

    private void applyRequest(Role role, RoleRequestDTO requestDTO) {
        role.setName(requestDTO.getName());
        role.setDescription(requestDTO.getDescription());
        role.setActive(requestDTO.isActive());

        if (requestDTO.getPermissionIds() != null) {
            List<Permission> permissions = permissionRepository.findAllById(requestDTO.getPermissionIds());
            role.setPermissions(permissions);
        }
    }

    private RoleResponseDTO toResponseDTO(Role role) {
        RoleResponseDTO dto = new RoleResponseDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setActive(role.isActive());
        if (role.getPermissions() != null) {
            dto.setPermissions(role.getPermissions().stream()
                    .map(permission -> permission.getName())
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
