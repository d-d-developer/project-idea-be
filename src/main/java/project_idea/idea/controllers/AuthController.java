package project_idea.idea.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.payloads.NewUserDTO;
import project_idea.idea.payloads.UserLoginDTO;
import project_idea.idea.payloads.UserLoginResponseDTO;
import project_idea.idea.services.AuthService;
import project_idea.idea.services.UsersService;

import java.util.stream.Collectors;

@RestController
@Tag(name = "Authentication", description = "Authentication management APIs")
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UsersService usersService;

    @Operation(summary = "Login user", description = "Authenticate a user and return a JWT token")
    @ApiResponse(responseCode = "200", description = "Successfully authenticated")
    @PostMapping("/login")
    public UserLoginResponseDTO login(@RequestBody UserLoginDTO body) {
        return new UserLoginResponseDTO(this.authService.checkCredentialsAndGenerateToken(body));
    }

    @Operation(summary = "Register user", description = "Register a new user")
    @ApiResponse(responseCode = "201", description = "User successfully created")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User save(@RequestBody @Validated NewUserDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            String message = validationResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.joining(". "));
            throw new BadRequestException("There were errors in the payload! " + message);
        }

        return this.usersService.save(body);
    }
}