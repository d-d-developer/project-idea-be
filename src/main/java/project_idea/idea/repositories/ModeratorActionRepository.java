package project_idea.idea.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project_idea.idea.entities.ModeratorAction;
import project_idea.idea.entities.User;
import project_idea.idea.entities.Post;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModeratorActionRepository extends JpaRepository<ModeratorAction, UUID> {
    Page<ModeratorAction> findByTargetUser(User user, Pageable pageable);
    Page<ModeratorAction> findByTargetPost(Post post, Pageable pageable);
    Page<ModeratorAction> findByModerator(User moderator, Pageable pageable);
    List<ModeratorAction> findByTargetUserOrderByCreatedAtDesc(User user);
}
