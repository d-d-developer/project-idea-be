package project_idea.idea.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project_idea.idea.entities.Role;
import project_idea.idea.entities.SocialProfile;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.payloads.NewUserDTO;
import project_idea.idea.payloads.PartialUserUpdateDTO;
import project_idea.idea.repositories.UsersRepository;
import project_idea.idea.services.RoleService;
import project_idea.idea.services.CategoryService;
import project_idea.idea.services.UsernameSuggestionService;

import java.util.HashMap;
import java.util.UUID;

@Service
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private PasswordEncoder bcrypt;
    
    // other autowired dependencies

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UsernameSuggestionService usernameSuggestionService;

    public User save(NewUserDTO body) {
        this.usersRepository.findByEmail(body.email()).ifPresent(
                user -> {
                    throw new BadRequestException("Email address " + body.email() + " is already in use!");
                }
        );

        User newUser = new User(body.email(), bcrypt.encode(body.password()));
        newUser.getSocialProfile().setFirstName(body.firstName());
        newUser.getSocialProfile().setLastName(body.lastName());
        newUser.getSocialProfile().updateAvatarUrl();

        // Set username for social profile
        if (body.username() != null) {
            // Check if username is already taken
            this.usersRepository.findBySocialProfile_Username(body.username()).ifPresent(
                user -> {
                    throw new BadRequestException("Username " + body.username() + " is already taken!");
                }
            );
            newUser.getSocialProfile().setUsername(body.username());
        } else {
            // Generate username using UsernameSuggestionService
            newUser.getSocialProfile().setUsername(usernameSuggestionService.generateUsername(/*body.email()*/));
        }
        
        // Add interests if provided
        if (body.interests() != null) {
            body.interests().forEach(categoryId -> newUser.getInterests().add(categoryService.getCategoryById(categoryId)));
        }

        Role userRole = roleService.getRoleByName("USER");

        newUser.getRoles().add(userRole);

        User savedUser = this.usersRepository.save(newUser);

        return savedUser;
    }

    public Page<User> findAll(int page, int size, String sortBy) {
        if (size > 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        return this.usersRepository.findAll(pageable);
    }

    public User findById(UUID userId) {
        return this.usersRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
    }

    public User findByIdAndUpdate(UUID userId, NewUserDTO body) {
        User found = this.findById(userId);

        if (!found.getEmail().equals(body.email())) {
            this.usersRepository.findByEmail(body.email()).ifPresent(
                    user -> {
                        throw new BadRequestException("Email address " + body.email() + " is already in use!");
                    }
            );
        }

        found.getSocialProfile().setFirstName(body.firstName());
        found.getSocialProfile().setLastName(body.lastName());
        found.setEmail(body.email());
        found.setPassword(body.password());
        found.getSocialProfile().updateAvatarUrl();

        if (body.interests() != null) {
            found.getInterests().clear();
            body.interests().forEach(categoryId -> found.getInterests().add(categoryService.getCategoryById(categoryId)));
        }

        return this.usersRepository.save(found);
    }

    public void findByIdAndDelete(UUID userId) {
        User found = this.findById(userId);
        this.usersRepository.delete(found);
    }

    public User findByEmail(String email) {
        return this.usersRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User with email " + email + " not found"));
    }

    public User addRoleToUser(UUID userId, UUID roleId) {
        User user = findById(userId);
        Role role = roleService.getRoleById(roleId);
        user.getRoles().add(role);
        return usersRepository.save(user);
    }
    
    public User removeRoleFromUser(UUID userId, UUID roleId) {
        User user = findById(userId);
        Role role = roleService.getRoleById(roleId);
        user.getRoles().remove(role);
        return usersRepository.save(user);
    }

    public User findByIdAndPatch(UUID userId, PartialUserUpdateDTO body) {
        User found = this.findById(userId);

        if (body.email() != null && !found.getEmail().equals(body.email())) {
            this.usersRepository.findByEmail(body.email()).ifPresent(
                    user -> {
                        throw new BadRequestException("Email address " + body.email() + " is already in use!");
                    }
            );
            found.setEmail(body.email());
        }

        if (body.firstName() != null) {
            found.getSocialProfile().setFirstName(body.firstName());
            found.getSocialProfile().updateAvatarUrl();
        }

        if (body.lastName() != null) {
            found.getSocialProfile().setLastName(body.lastName());
            found.getSocialProfile().updateAvatarUrl();
        }

        if (body.password() != null) {
            found.setPassword(bcrypt.encode(body.password()));
        }

        if (body.interests() != null) {
            found.getInterests().clear();
            body.interests().forEach(categoryId -> found.getInterests().add(categoryService.getCategoryById(categoryId)));
        }

        return this.usersRepository.save(found);
    }
}
