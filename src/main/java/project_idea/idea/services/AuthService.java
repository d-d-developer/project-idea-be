package project_idea.idea.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.UnauthorizedException;
import project_idea.idea.payloads.UserLoginDTO;
import project_idea.idea.tools.JWT;

@Service
public class AuthService {
	@Autowired
	private UsersService usersService;

	@Autowired
	private JWT jwt;

	@Autowired
	private PasswordEncoder bcrypt;

	public String checkCredentialsAndGenerateToken(UserLoginDTO body) {

		User found = this.usersService.findByEmail(body.email());

		if (bcrypt.matches(body.password(), found.getPassword())) {

			String accessToken = jwt.createToken(found);

			return accessToken;
		} else {

			throw new UnauthorizedException("Invalid credentials!");
		}
	}

}