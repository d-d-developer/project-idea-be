package project_idea.idea.controllers;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.payloads.ErrorsResponseDTO;
import project_idea.idea.payloads.NewUserDTO;
import project_idea.idea.services.UsersService;

import java.util.UUID;

@RestController
@Tag(name = "Users", description = "APIs for user management and profile operations")
@RequestMapping("/users")
@ApiResponses(value = {
    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
    @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
    @ApiResponse(responseCode = "404", description = "User not found",
        content = @Content(schema = @Schema(implementation = ErrorsResponseDTO.class)))
})
@SecurityRequirement(name = "bearerAuth")
public class UsersController {
    @Autowired
    private UsersService usersService;

    @Operation(
        summary = "Get all users",
        description = "Retrieve all users",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved users",
                content = @Content(schema = @Schema(implementation = User.class))
            )
        }
    )
    @GetMapping
    public Page<User> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                              @RequestParam(defaultValue = "id") String sortBy) {
        return this.usersService.findAll(page, size, sortBy);
    }

    @Operation(
        summary = "Get current user profile",
        description = "Retrieve the profile information of the currently authenticated user",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Profile retrieved successfully",
                content = @Content(schema = @Schema(implementation = User.class))
            )
        }
    )
    @GetMapping("/me")
    public User getProfile(@AuthenticationPrincipal User currentAuthenticatedUser) {
        return currentAuthenticatedUser;
    }

    @PutMapping("/me")
    public User updateProfile(@AuthenticationPrincipal User currentAuthenticatedUser, @RequestBody @Validated NewUserDTO body) {
        return this.usersService.findByIdAndUpdate(currentAuthenticatedUser.getId(), body);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfile(@AuthenticationPrincipal User currentAuthenticatedUser) {
        this.usersService.findByIdAndDelete(currentAuthenticatedUser.getId());
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable UUID userId) {
        return this.usersService.findById(userId);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User findByIdAndUpdate(@PathVariable UUID userId, @RequestBody @Validated NewUserDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            validationResult.getAllErrors().forEach(System.out::println);
            throw new BadRequestException("There were errors in the payload!");
        }
        return this.usersService.findByIdAndUpdate(userId, body);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void findByIdAndDelete(@PathVariable UUID userId) {
        this.usersService.findByIdAndDelete(userId);
    }

}
