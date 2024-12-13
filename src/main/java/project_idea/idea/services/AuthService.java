package project_idea.idea.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project_idea.idea.entities.User;
import project_idea.idea.enums.UserStatus;
import project_idea.idea.exceptions.UnauthorizedException;
import project_idea.idea.payloads.user.UserLoginDTO;
import project_idea.idea.tools.JWT;

import java.time.LocalDateTime;

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

        // Check if user is banned
        if (found.getStatus() == UserStatus.BANNED) {
            throw new UnauthorizedException("Your account has been banned. Reason: " + found.getModerationReason());
        }

        // Check if user is suspended
        if (found.getStatus() == UserStatus.SUSPENDED) {
            if (found.getSuspensionEndDate().isAfter(LocalDateTime.now())) {
                throw new UnauthorizedException("Your account is suspended until " + 
                    found.getSuspensionEndDate() + ". Reason: " + found.getModerationReason());
            } else {
                // If suspension period is over, reactivate the account
                found.setStatus(UserStatus.ACTIVE);
                found.setSuspensionEndDate(null);
                found.setModerationReason(null);
                usersService.save(found);
            }
        }

        if (bcrypt.matches(body.password(), found.getPassword())) {
            return jwt.createToken(found);
        } else {
            throw new UnauthorizedException("Invalid credentials!");
        }
    }
}
