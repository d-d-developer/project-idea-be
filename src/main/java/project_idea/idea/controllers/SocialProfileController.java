package project_idea.idea.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project_idea.idea.entities.SocialProfile;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.UnauthorizedException;
import project_idea.idea.payloads.socialProfile.PartialSocialProfileUpdateDTO;
import project_idea.idea.payloads.socialProfile.SocialProfileUpdateDTO;
import project_idea.idea.services.SocialProfileService;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;

@RestController
@RequestMapping("/social-profiles")
@Tag(name = "Social Profiles", description = "APIs for managing user social profiles")
public class SocialProfileController {

    private static final Logger logger = LoggerFactory.getLogger(SocialProfileController.class);

    @Autowired
    private SocialProfileService socialProfileService;
    
    @Autowired
    private PagedResourcesAssembler<SocialProfile> pagedResourcesAssembler;

    @GetMapping("/{username}")
    @Operation(summary = "Get user's public profile by username")
    public SocialProfile getProfileByUsername(@PathVariable String username) {
        return socialProfileService.getSocialProfileByUsername(username);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user's social profile")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SocialProfile> getCurrentUserProfile(@AuthenticationPrincipal User currentUser) throws UnauthorizedException {
        if (currentUser == null) {
            logger.error("Attempted to access /me endpoint without authentication");
            throw new UnauthorizedException("Authentication required");
        }
        logger.debug("Accessing profile for user: {}", currentUser.getEmail());
        SocialProfile profile = socialProfileService.getSocialProfileByUserId(currentUser.getId());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user's social profile")
    @SecurityRequirement(name = "bearerAuth")
    public SocialProfile updateCurrentUserProfile(@AuthenticationPrincipal User currentUser,
                                                  @RequestBody @Valid SocialProfileUpdateDTO updatedProfile) {
        return socialProfileService.updateSocialProfile(currentUser.getId(), updatedProfile);
    }

    @PatchMapping(path = "/me/avatar", consumes = "multipart/form-data")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Upload profile avatar",
        description = "Upload a new avatar image for the current user's profile. Supports JPG, PNG, and GIF formats up to 5MB."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Avatar successfully uploaded"),
        @ApiResponse(responseCode = "400", description = "Invalid file format or size"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Error uploading file")
    })
    public SocialProfile uploadAvatar(@AuthenticationPrincipal User currentUser,
                                      @RequestParam(name = "avatar", required = true) MultipartFile file) {
        return this.socialProfileService.uploadAvatar(currentUser.getId(), file);
    }

    @GetMapping
    @Operation(
        summary = "Get all social profiles",
        description = "Retrieve a paginated list of all social profiles",
        parameters = {
            @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
            @Parameter(name = "size", description = "Number of items per page", example = "10"),
            @Parameter(name = "sortBy", description = "Field to sort by", example = "username")
        }
    )
    public PagedModel<EntityModel<SocialProfile>> getAllProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sortBy
    ) {
        return pagedResourcesAssembler.toModel(socialProfileService.getAllProfiles(page, size, sortBy));
    }

    @PatchMapping("/me")
    @Operation(summary = "Partially update current user's social profile")
    @SecurityRequirement(name = "bearerAuth")
    public SocialProfile patchCurrentUserProfile(
            @AuthenticationPrincipal User currentUser,
            @RequestBody PartialSocialProfileUpdateDTO updatedProfile) {
        return socialProfileService.patchSocialProfile(currentUser.getId(), updatedProfile);
    }
}
