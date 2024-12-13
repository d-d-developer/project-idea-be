package project_idea.idea.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project_idea.idea.entities.Thread;
import project_idea.idea.entities.SocialProfile;

import java.util.UUID;

@Repository
public interface ThreadRepository extends JpaRepository<Thread, UUID> {
    Page<Thread> findByAuthorProfile(SocialProfile authorProfile, Pageable pageable);
    Page<Thread> findByPostsId(UUID postId, Pageable pageable);
}
