package project_idea.idea.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import project_idea.idea.entities.Post;
import project_idea.idea.entities.SocialProfile;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.repositories.PostRepository;
import project_idea.idea.utils.LanguageUtils;

import java.util.Arrays;
import java.util.UUID;

@Service
public class PostService {
    @Autowired
    private PostRepository<Post> postRepository;

    @Autowired
    private SocialProfileService socialProfileService;
    
    private static final String TYPE_PROJECT = "PROJECT";
    private static final String TYPE_SURVEY = "SURVEY";

    public Page<Post> getAllPosts(String type, int page, int size, String sortBy, String language, User currentUser) {
        if (size > 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        boolean isAdmin = currentUser != null && 
            currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));

        // If admin, show all posts
        if (isAdmin) {
            if (language != null) {
                return postRepository.findByLanguage(language.toLowerCase(), pageable);
            }
            return postRepository.findAll(pageable);
        }
        
        // If authenticated user but not admin, show featured posts and their own posts
        if (currentUser != null) {
            return postRepository.findByFeaturedTrueOrAuthorProfileId(true, 
                currentUser.getSocialProfile().getId(), pageable);
        }
        
        // If not authenticated, show only featured posts
        return postRepository.findByFeaturedTrue(pageable);
    }

    public Page<Post> getMyPosts(User currentUser, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return postRepository.findByAuthorProfileId(currentUser.getSocialProfile().getId(), pageable);
    }

    public Page<Post> getFeaturedPosts(int page, int size, String sortBy) {
        if (size > 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return postRepository.findByFeaturedTrue(pageable);
    }

    public Page<Post> getPostsBySocialProfile(UUID profileId, int page, int size, String sortBy) {
        return getPostsBySocialProfile(profileId, page, size, sortBy, null);
    }

    public Page<Post> getPostsBySocialProfile(UUID profileId, int page, int size, String sortBy, User currentUser) {
        if (size > 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        
        return currentUser != null ? postRepository.findByAuthorProfileId(profileId, pageable) 
            : postRepository.findByAuthorProfileIdAndFeaturedTrue(profileId, pageable);
    }

    public Post getPostById(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + id));
    }
}
