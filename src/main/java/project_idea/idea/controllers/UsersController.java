package project_idea.idea.controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project_idea.idea.entities.User;

import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.payloads.user.PartialUserUpdateDTO;
import project_idea.idea.services.UsersService;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "APIs for user management and users operations")
public class UsersController {
    @Autowired
    private UsersService usersService;

    @Autowired
    private PagedResourcesAssembler<User> pagedResourcesAssembler;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
            summary = "Get all users",
            description = "Retrieves a paged list of all users. Requires ADMIN authority.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval of users",
                            content = @Content(mediaType = "application/hal+json",
                                    schema = @Schema(implementation = PagedModel.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN authority")
            }
    )
    public PagedModel<EntityModel<User>> findAll(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "id") String sortBy
    ) {
        Page<User> userPage = this.usersService.findAll(page, size, sortBy);
        return pagedResourcesAssembler.toModel(userPage);
    }

    @GetMapping("/me")
    @JsonIgnore(false)
    @Operation(
        summary = "Get current user",
        description = "Retrieve the information of the currently authenticated user",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "User retrieved successfully",
                content = @Content(schema = @Schema(implementation = User.class))
            )
        }
    )
    public User getUser(@AuthenticationPrincipal User currentAuthenticatedUser) {
        return currentAuthenticatedUser;
    }

    @PutMapping("/me")
    @Operation(
        summary = "Update current user (full update)",
        description = "Update all fields of the currently authenticated user",
        responses = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
        }
    )
    @SecurityRequirement(name = "bearerAuth")
    public User updateCurrentUser(
            @RequestBody @Validated PartialUserUpdateDTO body,
            BindingResult validationResult,
            @AuthenticationPrincipal User currentUser) {
        if (validationResult.hasErrors()) {
            throw new BadRequestException(validationResult.getAllErrors().toString());
        }
        return this.usersService.findByIdAndPatch(currentUser.getId(), body);
    }

    @PatchMapping("/me")
    @Operation(
        summary = "Partially update current user",
        description = "Update specific fields of the currently authenticated user",
        responses = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
        }
    )
    @SecurityRequirement(name = "bearerAuth")
    public User patchCurrentUser(
            @RequestBody @Validated PartialUserUpdateDTO body,
            BindingResult validationResult,
            @AuthenticationPrincipal User currentUser) {
        return this.usersService.findByIdAndPatch(currentUser.getId(), body);
    }

    @GetMapping("/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @JsonIgnore(false)
    @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.id")
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
    public User findByIdAndUpdate(@PathVariable UUID userId, @RequestBody @Validated PartialUserUpdateDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            validationResult.getAllErrors().forEach(System.out::println);
            throw new BadRequestException("There were errors in the payload!");
        }
        return this.usersService.findByIdAndPatch(userId, body);
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

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete current user",
        description = "Delete the authenticated user",
        responses = {
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
        }
    )
    public void deleteUser(@AuthenticationPrincipal User currentAuthenticatedUser) {
        this.usersService.findByIdAndDelete(currentAuthenticatedUser.getId());
    }
}
