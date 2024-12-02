package project_idea.idea.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import project_idea.idea.entities.Role;
import project_idea.idea.entities.User;
import project_idea.idea.payloads.NewUserDTO;
import project_idea.idea.repositories.RoleRepository;
import project_idea.idea.services.UsersService;

@Component
public class RolesInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(RolesInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UsersService usersService;

    @Override
    public void run(String... args) {
        try {
            // Initialize USER role if it doesn't exist
            if (!roleRepository.existsByName("USER")) {
                Role userRole = new Role("USER", "Standard user role", true);
                roleRepository.save(userRole);
                logger.info("Created USER role");
            }

            // Initialize ADMIN role if it doesn't exist
            if (!roleRepository.existsByName("ADMIN")) {
                Role adminRole = new Role("ADMIN", "Administrator role with full privileges", true);
                roleRepository.save(adminRole);
                logger.info("Created ADMIN role");
            }

            // Create default admin user if no users exist
            if (roleRepository.count() == 2 && usersService.findAll(0, 1, "id").getTotalElements() == 0) {
                NewUserDTO adminDTO = new NewUserDTO(
                    "Admin",
                    "User",
                    "admin@example.com",
                    "admin123"
                );
                
                User adminUser = usersService.save(adminDTO);
                Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
                
                usersService.addRoleToUser(adminUser.getId(), adminRole.getId());
                logger.info("Created default admin user");
            }

        } catch (Exception e) {
            logger.error("Error initializing roles: " + e.getMessage());
            throw new RuntimeException("Failed to initialize system roles", e);
        }
    }
}
