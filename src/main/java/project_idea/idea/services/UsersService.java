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
import project_idea.idea.payloads.RoleCreateDTO;
import project_idea.idea.repositories.UsersRepository;
import project_idea.idea.services.RoleService;
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

	public User save(NewUserDTO body) {
		// Verifica se l'email è già in uso
		this.usersRepository.findByEmail(body.email()).ifPresent(
				user -> {
					throw new BadRequestException("Email address " + body.email() + " is already in use!");
				}
		);

		User newUser = new User(body.name(), body.surname(), body.email(), bcrypt.encode(body.password()),
				"https://ui-avatars.com/api/?name=" + body.name() + "+" + body.surname());

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

		found.setName(body.name());
		found.setSurname(body.surname());
		found.setEmail(body.email());
		found.setPassword(body.password());
		found.setAvatarURL("https://ui-avatars.com/api/?name=" + body.name() + "+" + body.surname());

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

}
