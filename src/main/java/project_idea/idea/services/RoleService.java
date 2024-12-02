package project_idea.idea.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project_idea.idea.entities.Role;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.repositories.RoleRepository;

import java.util.List;
import java.util.UUID;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role createRole(String name, String description) {
        if (roleRepository.existsByName(name)) {
            throw new BadRequestException("Role with name " + name + " already exists");
        }
        
        Role role = new Role(name, description, false);
        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found"));
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Role not found"));
    }

    public void deleteRole(UUID id) {
        Role role = getRoleById(id);
        if (role.isSystemRole()) {
            throw new BadRequestException("Cannot delete system roles");
        }
        roleRepository.delete(role);
    }

    public Role updateRole(UUID id, String name, String description) {
        Role role = getRoleById(id);
        if (role.isSystemRole()) {
            throw new BadRequestException("Cannot modify system roles");
        }
        
        if (!role.getName().equals(name) && roleRepository.existsByName(name)) {
            throw new BadRequestException("Role with name " + name + " already exists");
        }
        
        role.setName(name);
        role.setDescription(description);
        return roleRepository.save(role);
    }
}
