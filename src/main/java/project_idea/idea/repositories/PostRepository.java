package project_idea.idea.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project_idea.idea.entities.Post;
import project_idea.idea.enums.PostStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository<T extends Post> extends JpaRepository<T, UUID> {
    List<T> findByFeaturedTrue();
    Page<T> findByFeaturedTrue(Pageable pageable);
    Page<T> findByStatus(PostStatus status, Pageable pageable);
    Page<T> findByAuthorProfileId(UUID authorId, Pageable pageable);
    Page<T> findByAuthorProfileIdAndFeaturedTrue(UUID authorId, Pageable pageable);
    Page<T> findByFeaturedTrueOrAuthorProfileId(boolean featured, UUID authorId, Pageable pageable);
    @Query("SELECT p FROM Post p WHERE p.type = :type")
    Page<T> findByPostType(String type, Pageable pageable);
    Page<T> findByLanguage(String language, Pageable pageable);
    Optional<T> findByIdAndLanguage(UUID id, String language);
    Page<T> findByLanguageAndFeaturedTrue(String lowerCase, Pageable pageable);
}
