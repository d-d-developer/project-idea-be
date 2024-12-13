package project_idea.idea.controllers;

import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project_idea.idea.entities.Role;
import project_idea.idea.payloads.user.RoleCreateDTO;
import project_idea.idea.services.RoleService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/roles")
@Tag(name = "Roles", description = "APIs for role management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('ADMIN')")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Role createRole(@RequestBody RoleCreateDTO roleDTO) {
        return roleService.createRole(roleDTO.name(), roleDTO.description());
    }

    @PutMapping("/{id}")
    public Role updateRole(@PathVariable UUID id, @RequestBody RoleCreateDTO roleDTO) {
        return roleService.updateRole(id, roleDTO.name(), roleDTO.description());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
    }
}
