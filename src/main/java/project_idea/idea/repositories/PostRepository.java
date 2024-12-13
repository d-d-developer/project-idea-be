package project_idea.idea.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project_idea.idea.entities.Category;
import project_idea.idea.entities.Post;
import project_idea.idea.enums.PostType;
import project_idea.idea.enums.Visibility;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PostRepository<T extends Post> extends JpaRepository<T, UUID> {
    List<T> findByFeaturedTrue();
    Page<T> findByFeaturedTrue(Pageable pageable);
    Page<T> findByVisibility(Visibility visibility, Pageable pageable);
    Page<T> findByVisibilityNot(Visibility visibility, Pageable pageable);
    Page<T> findByAuthorProfileId(UUID authorId, Pageable pageable);
    Page<T> findByAuthorProfileIdAndFeaturedTrue(UUID authorId, Pageable pageable);
    Page<T> findByFeaturedTrueOrAuthorProfileId(boolean featured, UUID authorId, Pageable pageable);
    Page<T> findByType(PostType type, Pageable pageable);
    Page<T> findByTypeAndVisibilityNot(PostType type, Visibility visibility, Pageable pageable);
    Page<T> findByTypeAndLanguage(PostType type, String language, Pageable pageable);
    Page<T> findByTypeAndLanguageAndVisibilityNot(PostType type, String language, Visibility visibility, Pageable pageable);
    @Query("SELECT p FROM Post p WHERE p.type = :type")
    Page<T> findByPostType(PostType type, Pageable pageable);
    Page<T> findByLanguage(String language, Pageable pageable);
    Page<T> findByLanguageAndVisibilityNot(String language, Visibility visibility, Pageable pageable);
    Optional<T> findByIdAndLanguage(UUID id, String language);
    Page<T> findByLanguageAndFeaturedTrue(String lowerCase, Pageable pageable);

    Page<T> findByTypeAndVisibility(PostType type, Visibility visibility, Pageable pageable);

    Page<T> findByTypeInAndVisibility(List<PostType> types, Visibility visibility, Pageable pageable);

    Page<T> findByCategoriesInAndVisibility(Set<Category> categories, Visibility visibility, Pageable pageable);
    Page<T> findByAuthorProfileIdAndType(UUID authorId, PostType type, Pageable pageable);
}
