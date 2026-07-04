package com.gopitch.GoPitch.controller.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gopitch.GoPitch.domain.request.role.RoleRequestDTO;
import com.gopitch.GoPitch.domain.response.ResultPaginationDTO;
import com.gopitch.GoPitch.domain.response.role.RoleResponseDTO;
import com.gopitch.GoPitch.service.RoleService;
import com.gopitch.GoPitch.util.annotation.ApiMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @ApiMessage("Fetch all roles")
    public ResponseEntity<ResultPaginationDTO<RoleResponseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(roleService.fetchAll(pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<RoleResponseDTO> getById(@PathVariable long id) {
        return ResponseEntity.ok(roleService.fetchById(id));
    }

    @PostMapping
    @ApiMessage("Create role")
    public ResponseEntity<RoleResponseDTO> create(@Valid @RequestBody RoleRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.create(requestDTO));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update role")
    public ResponseEntity<RoleResponseDTO> update(
            @PathVariable long id,
            @Valid @RequestBody RoleRequestDTO requestDTO) {
        return ResponseEntity.ok(roleService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete role")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
