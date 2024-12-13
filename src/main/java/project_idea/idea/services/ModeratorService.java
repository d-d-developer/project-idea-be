package project_idea.idea.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project_idea.idea.entities.ModeratorAction;
import project_idea.idea.enums.ModeratorActionType;
import project_idea.idea.entities.Post;
import project_idea.idea.enums.Visibility;
import project_idea.idea.entities.User;
import project_idea.idea.enums.UserStatus;
import project_idea.idea.exceptions.BadRequestException;
import project_idea.idea.exceptions.NotFoundException;
import project_idea.idea.repositories.ModeratorActionRepository;
import project_idea.idea.repositories.PostRepository;
import project_idea.idea.repositories.UsersRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ModeratorService {
    @Autowired
    private ModeratorActionRepository moderatorActionRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PostRepository<Post> postRepository;

    @Transactional
    public ModeratorAction banUser(UUID targetUserId, String reason, User moderator) {
        User targetUser = usersRepository.findById(targetUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (targetUser.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
            throw new BadRequestException("Cannot ban an admin user");
        }

        targetUser.setStatus(UserStatus.BANNED);
        targetUser.setModerationReason(reason);
        targetUser.setLastModeratedAt(LocalDateTime.now());
        usersRepository.save(targetUser);

        ModeratorAction action = new ModeratorAction();
        action.setActionType(ModeratorActionType.USER_BAN);
        action.setModerator(moderator);
        action.setTargetUser(targetUser);
        action.setReason(reason);

        return moderatorActionRepository.save(action);
    }

    @Transactional
    public ModeratorAction suspendUser(UUID targetUserId, String reason, LocalDateTime duration, User moderator) {
        User targetUser = usersRepository.findById(targetUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (targetUser.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
            throw new BadRequestException("Cannot suspend an admin user");
        }

        targetUser.setStatus(UserStatus.SUSPENDED);
        targetUser.setModerationReason(reason);
        targetUser.setSuspensionEndDate(duration);
        targetUser.setLastModeratedAt(LocalDateTime.now());
        usersRepository.save(targetUser);

        ModeratorAction action = new ModeratorAction();
        action.setActionType(ModeratorActionType.USER_SUSPEND);
        action.setModerator(moderator);
        action.setTargetUser(targetUser);
        action.setReason(reason);
        action.setDuration(duration);

        return moderatorActionRepository.save(action);
    }

    @Transactional
    public ModeratorAction hidePost(UUID postId, String reason, User moderator) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        post.setVisibility(Visibility.HIDDEN);
        post.setModerationReason(reason);
        post.setModeratedBy(moderator);
        post.setLastModeratedAt(LocalDateTime.now());
        postRepository.save(post);

        ModeratorAction action = new ModeratorAction();
        action.setActionType(ModeratorActionType.POST_HIDE);
        action.setModerator(moderator);
        action.setTargetPost(post);
        action.setReason(reason);

        return moderatorActionRepository.save(action);
    }

    @Transactional
    public ModeratorAction deletePost(UUID postId, String reason, User moderator) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        post.setVisibility(Visibility.DELETED);
        post.setModerationReason(reason);
        post.setModeratedBy(moderator);
        post.setLastModeratedAt(LocalDateTime.now());
        postRepository.save(post);

        ModeratorAction action = new ModeratorAction();
        action.setActionType(ModeratorActionType.POST_DELETE);
        action.setModerator(moderator);
        action.setTargetPost(post);
        action.setReason(reason);

        return moderatorActionRepository.save(action);
    }

    public Page<ModeratorAction> getUserModeratorActions(UUID userId, int page, int size) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return moderatorActionRepository.findByTargetUser(user, pageable);
    }

    public Page<ModeratorAction> getPostModeratorActions(UUID postId, int page, int size) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return moderatorActionRepository.findByTargetPost(post, pageable);
    }

    @Transactional
    public void unbanUser(UUID userId, User moderator) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getStatus() != UserStatus.BANNED) {
            throw new BadRequestException("User is not banned");
        }

        user.setStatus(UserStatus.ACTIVE);
        user.setModerationReason(null);
        user.setLastModeratedAt(LocalDateTime.now());
        usersRepository.save(user);
    }

    @Transactional
    public void unsuspendUser(UUID userId, User moderator) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getStatus() != UserStatus.SUSPENDED) {
            throw new BadRequestException("User is not suspended");
        }

        user.setStatus(UserStatus.ACTIVE);
        user.setModerationReason(null);
        user.setSuspensionEndDate(null);
        user.setLastModeratedAt(LocalDateTime.now());
        usersRepository.save(user);
    }

    @Transactional
    public void unhidePost(UUID postId, User moderator) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (post.getVisibility() != Visibility.HIDDEN) {
            throw new BadRequestException("Post is not hidden");
        }

        post.setVisibility(Visibility.ACTIVE);
        post.setModerationReason(null);
        post.setModeratedBy(moderator);
        post.setLastModeratedAt(LocalDateTime.now());
        postRepository.save(post);
    }
}
