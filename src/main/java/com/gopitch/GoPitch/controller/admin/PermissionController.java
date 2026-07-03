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

import com.gopitch.GoPitch.domain.Permission;
import com.gopitch.GoPitch.domain.response.ResultPaginationDTO;
import com.gopitch.GoPitch.domain.response.permission.PermissionResponseDTO;
import com.gopitch.GoPitch.service.PermissionService;
import com.gopitch.GoPitch.util.annotation.ApiMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    @ApiMessage("Fetch all permissions")
    public ResponseEntity<ResultPaginationDTO<PermissionResponseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(permissionService.fetchAll(pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Fetch permission by id")
    public ResponseEntity<Permission> getById(@PathVariable long id) {
        return ResponseEntity.ok(permissionService.fetchById(id));
    }

    @PostMapping
    @ApiMessage("Create permission")
    public ResponseEntity<Permission> create(@Valid @RequestBody Permission permission) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.create(permission));
    }

    @PutMapping
    @ApiMessage("Update permission")
    public ResponseEntity<Permission> update(@Valid @RequestBody Permission permission) {
        return ResponseEntity.ok(permissionService.update(permission));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete permission")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
