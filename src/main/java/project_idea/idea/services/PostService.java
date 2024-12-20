package project_idea.idea.services;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project_idea.idea.entities.Post;
import project_idea.idea.entities.User;
import project_idea.idea.enums.PostType;
import project_idea.idea.enums.SortDirection;
import project_idea.idea.enums.Visibility;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.repositories.PostRepository;
import project_idea.idea.repositories.UsersRepository; // Added import for UsersRepository
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostService {
    @Autowired
    private PostRepository<Post> postRepository;

    @Autowired
    private UsersRepository usersRepository; // Added UsersRepository for user lookup

    @Autowired
    private Cloudinary cloudinaryUploader;

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/gif"};

    public Page<Post> getAllPosts(int page, int size, String sortBy, SortDirection direction, String language, PostType type, List<UUID> categoryIds, User currentUser) {
        if (size > 100) size = 100;
        Sort.Direction sortDirection = direction == SortDirection.DESC ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        boolean isAdmin = currentUser != null &&
            currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));

        // Handle category filtering
        if (categoryIds != null && !categoryIds.isEmpty()) {
            if (type != null) {
                return postRepository.findByTypeAndCategoriesIdInAndVisibilityNot(
                    type, categoryIds, Visibility.DELETED, pageable);
            }
            return postRepository.findByCategoriesIdInAndVisibilityNot(
                categoryIds, Visibility.DELETED, pageable);
        }

        // Handle type and language filters
        if (type != null && language != null) {
            return postRepository.findByTypeAndLanguageAndVisibilityNot(type, language.toLowerCase(), Visibility.DELETED, pageable);
        } else if (type != null) {
            return postRepository.findByTypeAndVisibilityNot(type, Visibility.DELETED, pageable);
        } else if (language != null) {
            return postRepository.findByLanguageAndVisibilityNot(language.toLowerCase(), Visibility.DELETED, pageable);
        }
        return postRepository.findByVisibilityNot(Visibility.DELETED, pageable);
    }

    public Page<Post> getMyPosts(User currentUser, int page, int size, String sortBy, SortDirection direction, PostType type, List<UUID> categoryIds) {
        Sort.Direction sortDirection = direction == SortDirection.DESC ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        if (categoryIds != null && !categoryIds.isEmpty()) {
            return type != null ? 
                postRepository.findByAuthorProfileIdAndTypeAndCategoriesIdIn(currentUser.getSocialProfile().getId(), type, categoryIds, pageable) :
                postRepository.findByAuthorProfileIdAndCategoriesIdIn(currentUser.getSocialProfile().getId(), categoryIds, pageable);
        }
        return type != null ? 
            postRepository.findByAuthorProfileIdAndType(currentUser.getSocialProfile().getId(), type, pageable) :
            postRepository.findByAuthorProfileId(currentUser.getSocialProfile().getId(), pageable);
    }

    public Page<Post> getFeaturedPosts(int page, int size, String sortBy, SortDirection direction) {
        if (size > 100) size = 100;
        Sort.Direction sortDirection = direction == SortDirection.DESC ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return postRepository.findByFeaturedTrue(pageable);
    }

    public Page<Post> getPostsBySocialProfile(UUID profileId, int page, int size, String sortBy, SortDirection direction, PostType type, List<UUID> categoryIds, User currentUser) {
        if (size > 100) size = 100;
        Sort.Direction sortDirection = direction == SortDirection.DESC ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        if (categoryIds != null && !categoryIds.isEmpty()) {
            return type != null ? 
                postRepository.findByTypeAndCategoriesIdInAndFeaturedTrue(type, categoryIds, pageable) :
                postRepository.findByCategoriesIdInAndFeaturedTrue(categoryIds, pageable);
        }
        return type != null ? postRepository.findByAuthorProfileIdAndType(profileId, type, pageable)
                           : postRepository.findByAuthorProfileId(profileId, pageable);
    }

    public Post getPostById(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + id));
    }

    public Post uploadFeaturedImage(UUID postId, MultipartFile file, String altText, User currentUser) {
        Post post = getPostById(postId);
        
        if (!post.getAuthorProfile().getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Only post author can update featured image");
        }

        validateImage(file);

        try {
            // Delete existing image if present
            if (post.getFeaturedImagePublicId() != null) {
                cloudinaryUploader.uploader().destroy(post.getFeaturedImagePublicId(), Map.of());
            }

            Map<String, String> options = new HashMap<>();
            options.put("folder", "featured-images");

            Map uploadResult = cloudinaryUploader.uploader().upload(file.getBytes(), options);

            post.setFeaturedImageUrl((String) uploadResult.get("url"));
            post.setFeaturedImagePublicId((String) uploadResult.get("public_id"));
            post.setFeaturedImageAlt(altText);

            return postRepository.save(post);
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload image: " + e.getMessage());
        }
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("No image was uploaded");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BadRequestException("Image size must not exceed 5MB");
        }

        String contentType = file.getContentType();
        if (!Arrays.asList(ALLOWED_IMAGE_TYPES).contains(contentType)) {
            throw new BadRequestException("Only JPEG, PNG and GIF images are allowed");
        }
    }

    public Page<Post> getSuggestedPosts(User currentUser, int page, int size, String sortBy, SortDirection direction) {
        if (size > 100) size = 100;
        Sort.Direction sortDirection = direction == SortDirection.DESC ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        // Get posts based on user type
        switch (currentUser.getUserType()) {
            case PROFESSIONAL:
                return postRepository.findByTypeAndVisibility(
                    PostType.INQUIRY, Visibility.ACTIVE, pageable);
                
            case INVESTOR:
                return postRepository.findByTypeInAndVisibility(
                    List.of(PostType.FUNDRAISER, PostType.PROJECT), 
                    Visibility.ACTIVE,
                    pageable);
                
            case CREATOR:
            default:
                return postRepository.findByCategoriesInAndVisibility(
                    currentUser.getInterests(), Visibility.ACTIVE, pageable);
        }
    }

    public Page<Post> getPostsByUsername(String username, int page, int size, String sortBy, 
                                        SortDirection direction, PostType type, 
                                        List<UUID> categoryIds, User currentUser) {
        if (size > 100) size = 100;
        Sort.Direction sortDirection = direction == SortDirection.DESC ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        User targetUser = usersRepository.findBySocialProfile_Username(username)
            .orElseThrow(() -> new NotFoundException("User not found with username: " + username));
            
        return getPostsBySocialProfile(targetUser.getSocialProfile().getId(), page, size, sortBy, direction, type, categoryIds, currentUser);
    }

    public Page<Post> searchPosts(
            String query,
            int page,
            int size,
            String sortBy,
            SortDirection direction,
            String language,
            String username,
            PostType type,
            List<UUID> categoryIds,
            User currentUser) {
        
        if (query == null || query.trim().isEmpty()) {
            throw new BadRequestException("Search query cannot be empty");
        }
        
        if (query.length() < 2) {
            throw new BadRequestException("Search query must be at least 2 characters long");
        }
        
        Sort.Direction sortDirection = direction == SortDirection.DESC ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        return postRepository.searchPosts(query.trim(), language, type, username, categoryIds, pageable);
    }
}
