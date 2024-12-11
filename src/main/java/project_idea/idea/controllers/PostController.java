package project_idea.idea.controllers;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project_idea.idea.entities.Post;
import project_idea.idea.entities.SocialProfile;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.services.PostService;

import java.util.UUID;

@RestController
@RequestMapping("/posts")
@Tag(name = "Posts", description = "APIs for managing all types of posts (surveys and projects)")
@SecurityRequirement(name = "bearerAuth")
public class PostController {
    @Autowired
    private PostService postService;
    private static final String POST_TYPE_ALL = "ALL";

    @Autowired
    private PagedResourcesAssembler<Post> pagedResourcesAssembler;

    @GetMapping
    @Operation(summary = "Get all posts")
    public PagedModel<EntityModel<Post>> getAllPosts(
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String language,
            @AuthenticationPrincipal User currentUser) {
        
        Page<Post> postPage = postService.getAllPosts(
            type != null ? type : POST_TYPE_ALL, 
            0, size, sortBy, language, currentUser);
        return pagedResourcesAssembler.toModel(postPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get post by ID")
    public Post getPostById(@PathVariable UUID id) {
        return postService.getPostById(id);
    }

    @GetMapping("/profile/{profileId}")
    @Operation(summary = "Get posts by social profile")
    public PagedModel<EntityModel<Post>> getPostsBySocialProfile(
            @PathVariable UUID profileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Nullable @AuthenticationPrincipal User currentUser) {
        return pagedResourcesAssembler.toModel(postService.getPostsBySocialProfile(profileId, page, size, sortBy, currentUser));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user's posts")
    @SecurityRequirement(name = "bearerAuth")
    public PagedModel<EntityModel<Post>> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @AuthenticationPrincipal User currentUser) {
        Page<Post> postPage = postService.getMyPosts(currentUser, page, size, sortBy);
        return pagedResourcesAssembler.toModel(postPage);
    }

    @PatchMapping(path = "/{postId}/featured-image", consumes = "multipart/form-data")
    @Operation(summary = "Upload or update post featured image")
    public Post uploadFeaturedImage(@PathVariable UUID postId,
                                    @RequestParam("image") MultipartFile file,
                                    @RequestParam(required = false) String altText,
                                    @AuthenticationPrincipal User currentUser) {
        return postService.uploadFeaturedImage(postId, file, altText, currentUser);
    }
}
