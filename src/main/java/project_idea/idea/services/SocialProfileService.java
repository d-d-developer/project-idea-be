package project_idea.idea.services;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project_idea.idea.entities.SocialProfile;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.repositories.SocialProfileRepository;
import project_idea.idea.repositories.UsersRepository;
import project_idea.idea.payloads.socialProfile.PartialSocialProfileUpdateDTO;
import project_idea.idea.payloads.socialProfile.SocialProfileUpdateDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SocialProfileService {

    @Autowired
    private SocialProfileRepository socialProfileRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private Cloudinary cloudinaryUploader;

    @JsonIgnore
    @Transactional(readOnly = true)
    public SocialProfile getSocialProfileByUserId(UUID userId) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return user.getSocialProfile();
    }

    public SocialProfile getSocialProfileByUsername(String username) {
        return socialProfileRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Profile not found for username: " + username));
    }

    public Page<SocialProfile> getAllProfiles(int page, int size, String sortBy) {
        if (size > 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return socialProfileRepository.findAll(pageable);
    }

    public SocialProfile updateSocialProfile(UUID userId, SocialProfileUpdateDTO updatedProfile) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        SocialProfile existingProfile = user.getSocialProfile();

        // Validate all required fields are present
        if (updatedProfile.username() == null || updatedProfile.firstName() == null || 
            updatedProfile.lastName() == null) {
            throw new BadRequestException("All fields are required for complete profile update");
        }

        // Check username uniqueness only if it's different from current
        if (!updatedProfile.username().equals(existingProfile.getUsername())) {
            if (socialProfileRepository.findByUsername(updatedProfile.username()).isPresent()) {
                throw new BadRequestException("Username already taken");
            }
            existingProfile.setUsername(updatedProfile.username());
        }

        // Update all fields
        existingProfile.setFirstName(updatedProfile.firstName());
        existingProfile.setLastName(updatedProfile.lastName());
        existingProfile.updateAvatarUrl();

        existingProfile.setBio(updatedProfile.bio());
        
        existingProfile.setLinks(updatedProfile.links() != null ? 
            updatedProfile.links() : new HashMap<>());

        return socialProfileRepository.save(existingProfile);
    }

    public SocialProfile patchSocialProfile(UUID userId, PartialSocialProfileUpdateDTO updatedProfile) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        SocialProfile existingProfile = user.getSocialProfile();

        // Only update fields that are present in the request
        if (updatedProfile.username() != null && !updatedProfile.username().equals(existingProfile.getUsername())) {
            if (socialProfileRepository.findByUsername(updatedProfile.username()).isPresent()) {
                throw new BadRequestException("Username already taken");
            }
            existingProfile.setUsername(updatedProfile.username());
        }

        if (updatedProfile.firstName() != null) {
            existingProfile.setFirstName(updatedProfile.firstName());
            existingProfile.updateAvatarUrl();
        }
        
        if (updatedProfile.lastName() != null) {
            existingProfile.setLastName(updatedProfile.lastName());
            existingProfile.updateAvatarUrl();
        }

        if (updatedProfile.bio() != null) {
            existingProfile.setBio(updatedProfile.bio());
        }

        if (updatedProfile.links() != null) {
            Map<String, String> currentLinks = new HashMap<>(existingProfile.getLinks());
            currentLinks.putAll(updatedProfile.links());
            existingProfile.setLinks(currentLinks);
        }

        return socialProfileRepository.save(existingProfile);
    }

    public SocialProfile uploadAvatar(UUID userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("No file was uploaded");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Only image files are allowed");
        }

        // Validate file size (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BadRequestException("File size must not exceed 5MB");
        }

        String url = null;
        try {
            Map<String, String> options = new HashMap<>();
            options.put("resource_type", "image");
            options.put("folder", "avatars");
            url = (String) cloudinaryUploader.uploader()
                .upload(file.getBytes(), options)
                .get("url");
        } catch (IOException e) {
            throw new BadRequestException("There were problems while uploading your file");
        }
        SocialProfile found = this.getSocialProfileByUserId(userId);
        found.setAvatarURL(url);
        found.setHasCustomAvatar(true);

        return this.socialProfileRepository.save(found);
    }

    public SocialProfile findById(UUID id) {
        return socialProfileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Social profile not found with id: " + id));
    }
}
