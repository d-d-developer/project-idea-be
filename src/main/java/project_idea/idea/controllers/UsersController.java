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
import project_idea.idea.payloads.PartialUserUpdateDTO;
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
        description = "Retrieve a paginated list of all users. Requires ADMIN role.",
        parameters = {
            @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
            @Parameter(name = "size", description = "Number of items per page", example = "10"),
            @Parameter(name = "sortBy", description = "Field to sort by", example = "id")
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

    @Operation(
        summary = "Update current user profile",
        description = "Update the authenticated user's profile information",
        responses = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
        }
    )
    @PutMapping("/me")
    public User updateProfile(@AuthenticationPrincipal User currentUser, @RequestBody @Validated NewUserDTO body) {
        return this.usersService.findByIdAndUpdate(currentUser.getId(), body);
    }

    @Operation(
        summary = "Partially update current user profile",
        description = "Update specific fields of the authenticated user's profile",
        responses = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
        }
    )
    @PatchMapping("/me")
    public User patchProfile(@AuthenticationPrincipal User currentUser, 
                            @RequestBody @Validated PartialUserUpdateDTO body) {
        return this.usersService.findByIdAndPatch(currentUser.getId(), body);
    }

    @GetMapping("/{userId}")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieve a specific user's information by their UUID",
        responses = {
            @ApiResponse(responseCode = "200", description = "User found successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
        }
    )
    public User findById(@PathVariable UUID userId) {
        return this.usersService.findById(userId);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
        summary = "Update user by ID",
        description = "Update a user's information. Requires ADMIN role.",
        responses = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found")
        }
    )
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
    @Operation(
        summary = "Delete user by ID",
        description = "Delete a user from the system. Requires ADMIN role.",
        responses = {
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found")
        }
    )
    public void findByIdAndDelete(@PathVariable UUID userId) {
        this.usersService.findByIdAndDelete(userId);
    }

    @Operation(
        summary = "Partially update user",
        description = "Update specific fields of a user's profile. Requires ADMIN role.",
        responses = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found")
        }
    )
    @PatchMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User patchUser(@PathVariable UUID userId, 
                         @RequestBody @Validated PartialUserUpdateDTO body) {
        return this.usersService.findByIdAndPatch(userId, body);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete current user profile",
        description = "Delete the authenticated user's own profile",
        responses = {
            @ApiResponse(responseCode = "204", description = "Profile successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
        }
    )
    public void deleteProfile(@AuthenticationPrincipal User currentAuthenticatedUser) {
        this.usersService.findByIdAndDelete(currentAuthenticatedUser.getId());
    }

    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
        summary = "Add role to user",
        description = "Assign a role to a user. Requires ADMIN role.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Role successfully added to user"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User or role not found")
        }
    )
    @Parameters({
        @Parameter(name = "userId", description = "ID of the user", required = true),
        @Parameter(name = "roleId", description = "ID of the role to add", required = true)
    })
    public User addRoleToUser(@PathVariable UUID userId, @PathVariable UUID roleId) {
        return this.usersService.addRoleToUser(userId, roleId);
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
        summary = "Remove role from user",
        description = "Remove a role from a user. Requires ADMIN role.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Role successfully removed from user"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User or role not found")
        }
    )
    @Parameters({
        @Parameter(name = "userId", description = "ID of the user", required = true),
        @Parameter(name = "roleId", description = "ID of the role to remove", required = true)
    })
    public User removeRoleFromUser(@PathVariable UUID userId, @PathVariable UUID roleId) {
        return this.usersService.removeRoleFromUser(userId, roleId);
    }

}
