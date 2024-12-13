package project_idea.idea.controllers;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project_idea.idea.entities.Post;
import project_idea.idea.entities.SocialProfile;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.services.PostService;
import project_idea.idea.enums.PostType;
import project_idea.idea.enums.SortDirection;
import project_idea.idea.config.PostResourceAssembler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/posts")
@Tag(name = "Posts", description = "APIs for managing all types of posts")
@SecurityRequirement(name = "bearerAuth")
public class PostController {
    private final PostService postService;
    private final PagedResourcesAssembler<Post> customPagedResourcesAssembler;
    private final PostResourceAssembler postResourceAssembler;

    @Autowired
    public PostController(PostService postService,
                         PagedResourcesAssembler<Post> customPagedResourcesAssembler,
                         PostResourceAssembler postResourceAssembler) {
        this.postService = postService;
        this.customPagedResourcesAssembler = customPagedResourcesAssembler;
        this.postResourceAssembler = postResourceAssembler;
    }

    @GetMapping
    @Operation(summary = "Get all posts")
    public ResponseEntity<Map<String, Object>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") 
            @Parameter(description = "Field to sort by") String sortBy,
            @RequestParam(defaultValue = "DESC") 
            @Parameter(description = "Sort direction (ASC or DESC)") SortDirection direction,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) 
            @Parameter(description = "Filter posts by type", 
                      schema = @Schema(implementation = PostType.class))
            PostType type,
            @AuthenticationPrincipal User currentUser) {

        Page<Post> postPage = postService.getAllPosts(page, size, sortBy, direction, language, type, currentUser);

        List<EntityModel<Post>> postModels = postPage.getContent().stream()
                .map(postResourceAssembler::toModel)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("posts", postModels);
        response.put("currentPage", postPage.getNumber());
        response.put("totalItems", postPage.getTotalElements());
        response.put("totalPages", postPage.getTotalPages());

        Link selfLink = linkTo(methodOn(PostController.class).getAllPosts(page, size, sortBy, direction, language, type, currentUser)).withSelfRel();
        if (postPage.hasNext()) {
            Link nextLink = linkTo(methodOn(PostController.class).getAllPosts(page + 1, size, sortBy, direction, language, type, currentUser)).withRel("next");
            response.put("nextPage", nextLink.getHref());
        }
        if (postPage.hasPrevious()) {
            Link prevLink = linkTo(methodOn(PostController.class).getAllPosts(page - 1, size, sortBy, direction, language, type, currentUser)).withRel("prev");
            response.put("prevPage", prevLink.getHref());
        }
        response.put("selfLink", selfLink.getHref());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get post by ID")
    public Post getPostById(@PathVariable UUID id) {
        return postService.getPostById(id);
    }

    @GetMapping("/profile/{profileId}")
    @Operation(summary = "Get posts by social profile")
    public ResponseEntity<Map<String, Object>> getPostsBySocialProfile(
            @PathVariable UUID profileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") 
            @Parameter(description = "Sort direction (ASC or DESC)") SortDirection direction,
            @Nullable @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) PostType type) {

        Page<Post> postPage = postService.getPostsBySocialProfile(profileId, page, size, sortBy, direction, type, currentUser);

        List<EntityModel<Post>> postModels = postPage.getContent().stream()
                .map(postResourceAssembler::toModel)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("posts", postModels);
        response.put("currentPage", postPage.getNumber());
        response.put("totalItems", postPage.getTotalElements());
        response.put("totalPages", postPage.getTotalPages());

        Link selfLink = linkTo(methodOn(PostController.class).getPostsBySocialProfile(profileId, page, size, sortBy, direction, currentUser, type)).withSelfRel();
        if (postPage.hasNext()) {
            Link nextLink = linkTo(methodOn(PostController.class).getPostsBySocialProfile(profileId, page + 1, size, sortBy, direction, currentUser, type)).withRel("next");
            response.put("nextPage", nextLink.getHref());
        }
        if (postPage.hasPrevious()) {
            Link prevLink = linkTo(methodOn(PostController.class).getPostsBySocialProfile(profileId, page - 1, size, sortBy, direction, currentUser, type)).withRel("prev");
            response.put("prevPage", prevLink.getHref());
        }
        response.put("selfLink", selfLink.getHref());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user's posts")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") 
            @Parameter(description = "Sort direction (ASC or DESC)") SortDirection direction,
            @RequestParam(required = false) PostType type,
            @AuthenticationPrincipal User currentUser) {

        Page<Post> postPage = postService.getMyPosts(currentUser, page, size, sortBy, direction, type);

        List<EntityModel<Post>> postModels = postPage.getContent().stream()
                .map(postResourceAssembler::toModel)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("posts", postModels);
        response.put("currentPage", postPage.getNumber());
        response.put("totalItems", postPage.getTotalElements());
        response.put("totalPages", postPage.getTotalPages());

        Link selfLink = linkTo(methodOn(PostController.class).getMyPosts(page, size, sortBy, direction, type, currentUser)).withSelfRel();
        if (postPage.hasNext()) {
            Link nextLink = linkTo(methodOn(PostController.class).getMyPosts(page + 1, size, sortBy, direction, type, currentUser)).withRel("next");
            response.put("nextPage", nextLink.getHref());
        }
        if (postPage.hasPrevious()) {
            Link prevLink = linkTo(methodOn(PostController.class).getMyPosts(page - 1, size, sortBy, direction, type, currentUser)).withRel("prev");
            response.put("prevPage", prevLink.getHref());
        }
        response.put("selfLink", selfLink.getHref());

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{postId}/featured-image", consumes = "multipart/form-data")
    @Operation(summary = "Upload or update post featured image")
    public Post uploadFeaturedImage(@PathVariable UUID postId,
                                    @RequestParam("image") MultipartFile file,
                                    @RequestParam(required = false) String altText,
                                    @AuthenticationPrincipal User currentUser) {
        return postService.uploadFeaturedImage(postId, file, altText, currentUser);
    }

    @GetMapping("/suggested")
    @Operation(summary = "Get suggested posts based on user type")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> getSuggestedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") 
            @Parameter(description = "Sort direction (ASC or DESC)") SortDirection direction,
            @AuthenticationPrincipal User currentUser) {

        Page<Post> postPage = postService.getSuggestedPosts(currentUser, page, size, sortBy, direction);

        List<EntityModel<Post>> postModels = postPage.getContent().stream()
                .map(postResourceAssembler::toModel)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("posts", postModels);
        response.put("currentPage", postPage.getNumber());
        response.put("totalItems", postPage.getTotalElements());
        response.put("totalPages", postPage.getTotalPages());

        Link selfLink = linkTo(methodOn(PostController.class).getSuggestedPosts(page, size, sortBy, direction, currentUser)).withSelfRel();
        if (postPage.hasNext()) {
            Link nextLink = linkTo(methodOn(PostController.class).getSuggestedPosts(page + 1, size, sortBy, direction, currentUser)).withRel("next");
            response.put("nextPage", nextLink.getHref());
        }
        if (postPage.hasPrevious()) {
            Link prevLink = linkTo(methodOn(PostController.class).getSuggestedPosts(page - 1, size, sortBy, direction, currentUser)).withRel("prev");
            response.put("prevPage", prevLink.getHref());
        }
        response.put("selfLink", selfLink.getHref());

        return ResponseEntity.ok(response);
    }
}
