package project_idea.idea.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project_idea.idea.entities.Role;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.payloads.NewUserDTO;
import project_idea.idea.payloads.PartialUserUpdateDTO;
import project_idea.idea.payloads.RoleCreateDTO;
import project_idea.idea.repositories.UsersRepository;
import project_idea.idea.services.RoleService;
import project_idea.idea.services.CategoryService;
import project_idea.idea.services.UsernameSuggestionService;
import project_idea.idea.tools.MailgunSender;

import java.util.UUID;

@Service
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private PasswordEncoder bcrypt;
    
    @Autowired
    private MailgunSender mailgunSender;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UsernameSuggestionService usernameSuggestionService;

    public User save(NewUserDTO body) {
        // Verifica se l'email è già in uso
        this.usersRepository.findByEmail(body.email()).ifPresent(
                user -> {
                    throw new BadRequestException("Email address " + body.email() + " is already in use!");
                }
        );

        User newUser = new User(body.name(), body.surname(), body.email(), bcrypt.encode(body.password()),
                "https://ui-avatars.com/api/?name=" + body.name() + "+" + body.surname());
        
        // Handle username
        String username = body.username();
        if (username != null) {
            // Check if username is already taken
            usersRepository.findByUsername(username).ifPresent(user -> {
                throw new BadRequestException("Username " + username + " is already taken!");
            });
            newUser.setUsername(username);
        } else {
            // Generate username if not provided
            newUser.setUsername(usernameSuggestionService.generateUsername(body.email()));
        }

        // Set bio if provided
        if (body.bio() != null) {
            newUser.setBio(body.bio());
        }
        
        // Add interests if provided
        if (body.interests() != null) {
            body.interests().forEach(categoryId -> newUser.getInterests().add(categoryService.getCategoryById(categoryId)));
        }

        Role userRole = roleService.getRoleByName("USER");

        newUser.getRoles().add(userRole);

        User savedUser = this.usersRepository.save(newUser);

        mailgunSender.sendRegistrationEmail(savedUser);

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

        // Handle username update if provided
        if (body.username() != null && !found.getUsername().equals(body.username())) {
            usersRepository.findByUsername(body.username()).ifPresent(
                    user -> {
                        throw new BadRequestException("Username " + body.username() + " is already taken!");
                    }
            );
            found.setUsername(body.username());
        }

        found.setName(body.name());
        found.setSurname(body.surname());
        found.setEmail(body.email());
        found.setPassword(body.password());

        // Update bio
        found.setBio(body.bio());
        
        // Update interests
        found.getInterests().clear();
        if (body.interests() != null) {
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

        if (body.username() != null && !found.getUsername().equals(body.username())) {
            this.usersRepository.findByUsername(body.username()).ifPresent(
                    user -> {
                        throw new BadRequestException("Username " + body.username() + " is already taken!");
                    }
            );
            found.setUsername(body.username());
        }

        if (body.name() != null) {
            found.setName(body.name());
        }

        if (body.surname() != null) {
            found.setSurname(body.surname());
        }

        if (body.password() != null) {
            found.setPassword(bcrypt.encode(body.password()));
        }

        if (body.bio() != null) {
            found.setBio(body.bio());
        }

        if (body.interests() != null) {
            found.getInterests().clear();
            body.interests().forEach(categoryId -> found.getInterests().add(categoryService.getCategoryById(categoryId)));
        }

        return this.usersRepository.save(found);
    }

}
